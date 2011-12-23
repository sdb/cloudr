package be.ellefant.droid.cloudapp

import android.content.Intent
import roboguice.service.RoboIntentService
import SharingService._
import android.text.ClipboardManager
import android.content.Context
import android.preference.PreferenceManager
import DatabaseHelper._
import android.net.Uri
import android.os.ParcelFileDescriptor.AutoCloseInputStream
import Cloud._
import android.widget.Toast
import ThreadUtils._
import FileType._

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
    	  api upload ("blabla." + extension, new AutoCloseInputStream(fd), fd.getStatSize)
    	  // TODO file name, check clouapp what it does when no name is supplied
    	  // TODO generate file name, e.g. 'Uploaded 2011-11-03 at 12:24' -> use user's current time and zone
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
      getContentResolver insert (CloudAppProvider.ContentUri, drop.toContentValues)
      logger debug ("New CloudAppItem created '%d'." format drop.id)
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
  private lazy val Name = classOf[SharingService].getSimpleName
}