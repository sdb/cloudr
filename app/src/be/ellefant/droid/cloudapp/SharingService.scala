package be.ellefant.droid.cloudapp

import android.content.Intent
import android.text.ClipboardManager
import android.content.Context
import android.preference.PreferenceManager
import android.net.Uri
import android.os.ParcelFileDescriptor.AutoCloseInputStream
import roboguice.service.RoboIntentService
import SharingService._
import Cloud._
import FileType._
import java.text.SimpleDateFormat
import java.util.Date

/**
 * Handles intents for sharing items (drops) to CloudApp.
 */
class SharingService extends RoboIntentService(Name)
    with Base.CloudrService
    with Injection.AccountManager
    with Injection.ApiFactory
    with Injection.ThreadUtil {

  def onHandleIntent(intent: Intent) = {
    def handleSendAction(api: Cloud): PartialFunction[String, Either[Error.Error, Drop]] = {
    	case "text/plain" =>
    	  val url = intent getStringExtra (Intent.EXTRA_TEXT)
    	  val title = intent getStringExtra (Intent.EXTRA_SUBJECT)
    	  api bookmark (title, url)
    	case Extension(extension) =>
    	  val u = Uri parse((intent.getExtras get ("android.intent.extra.STREAM")).toString)
    	  val fd = getContentResolver openFileDescriptor (u, "r")
        val name = UploadDateFormat.format(new Date())
    	  api upload ("%s.%s" format(name, extension), new AutoCloseInputStream(fd), fd.getStatSize)
    	case mt =>
    	  logger info ("mime type %s not supported" format mt)
    	  Left(Error.Other)
    }
    
    def sendFailure(error: Error.Error) = {
      val msg = error match { // TODO error messages
	    case Error.Auth => "Authorization failed!"
	    case Error.Limit => "Upload limit reached!"
	    case Error.Other => "Upload failed!"
	  }
      logger warn ("failure: " + msg)
      // TODO: on UI thread
      // val toast = Toast makeText(getApplicationContext, msg, Toast.LENGTH_SHORT)
      // toast show()
    }
    
    def sendSuccess(drop: Drop) = {
      val sharedPrefs = PreferenceManager getDefaultSharedPreferences this
      if (sharedPrefs getBoolean ("copy_url", true)) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE).asInstanceOf[ClipboardManager]
        clipboard setText (drop.url)
      }
      val provider = getContentResolver.acquireContentProviderClient(CloudAppProvider.ContentUri).getLocalContentProvider.asInstanceOf[CloudAppProvider]
      val db = provider.database.getWritableDatabase
      db beginTransaction()
      try {
        db insert(DatabaseHelper.TblItems, DatabaseHelper.ColId, drop.toContentValues) // TODO check first
        provider.context.getContentResolver notifyChange(CloudAppProvider.ContentUri, null)
        db setTransactionSuccessful()
      } catch {
        case e => // TODO
      }
      db endTransaction()
      logger debug("New CloudAppItem created '%d'." format drop.id)
    }
    
    (accountManager getAccountsByType(AccountType)) headOption match {
      case Some(account) ⇒
        val api = apiFactory create (account.name, accountManager getPassword account)
        Option(intent.getType) collect (handleSendAction(api)) foreach (_ fold (sendFailure _, sendSuccess _))
      case _ ⇒
        logger.info("no CloudApp account available")
    }
  }

}

object SharingService {
  val UploadDateFormat = new SimpleDateFormat("'Uploaded' yyyy-MM-dd 'at' HH:mm:ss")
  private lazy val Name = classOf[SharingService].getSimpleName
}