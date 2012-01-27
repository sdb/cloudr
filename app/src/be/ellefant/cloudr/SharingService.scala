package be.ellefant.cloudr

import android.content.Intent
import android.text.ClipboardManager
import android.content.Context
import android.preference.PreferenceManager
import android.net.Uri
import android.os.ParcelFileDescriptor.AutoCloseInputStream
import android.accounts.Account
import android.widget.Toast
import android.os.{ Handler, Looper }
import roboguice.service.RoboIntentService
import java.text.SimpleDateFormat
import java.util.Date
import SharingService._, Cloud._, ThreadUtils._, CloudAppManager._

/**
 * Handles intents for sharing items (drops) to CloudApp.
 */
class SharingService extends RoboIntentService(Name)
    with Base.CloudrService
    with Injection.AccountManager
    with Injection.ApiFactory
    with Injection.ThreadUtil
    with Injection.DropManager {

  private var handler: Handler = _

  override def onCreate() {
    super.onCreate()
    handler = new Handler(Looper.getMainLooper())
  }

  def onHandleIntent(intent: Intent) = {
      def handleUpload(api: Cloud, uri: Uri, name: String) = {
        logger debug ("uploading %s to %s" format (uri, name))
        val fd = getContentResolver openFileDescriptor (uri, "r")
        api upload (name, new AutoCloseInputStream(fd), fd.getStatSize)
      }
      def handleShare(api: Cloud, extension: String) = {
        val u = Uri parse ((intent.getExtras get ("android.intent.extra.STREAM")).toString)
        val name = "%s.%s" format (UploadDateFormat.format(new Date()), extension)
        handleUpload(api, u, name)
      }
      def handleView(api: Cloud) = {
        val u = intent.getData
        val name = u.getLastPathSegment
        handleUpload(api, u, name)
      }
      def handleBookmark(api: Cloud) = {
        val url = intent getStringExtra (Intent.EXTRA_TEXT)
        val title = intent getStringExtra (Intent.EXTRA_SUBJECT)
        api bookmark (title, url)
      }
      def handleSendAction(api: Cloud): PartialFunction[String, Either[Error.Error, Drop]] = { // TODO intent matchers
        case "text/plain" if intent.getAction == Intent.ACTION_SEND  ⇒ handleBookmark(api)
        case Extension(extension) if intent.getAction == Intent.ACTION_SEND ⇒ handleShare(api, extension)
        case Extension(extension) if intent.getAction == Intent.ACTION_VIEW ⇒ handleView(api)
      }

      def sendFailure(acc: Account, pwd: String)(error: Error.Error) = {
        val msg = error match {
          case Error.Auth ⇒
            accountManager.clearPassword(acc)
            accountManager.invalidateAuthToken(AccountType, pwd)
            "CloudApp authorization failed."
          case Error.Limit ⇒ "CloudApp upload limit reached."
          case Error.Other ⇒ "CloudApp upload failed."
        }
        logger warn ("failure: " + msg)

        handler post { () ⇒
          val toast = Toast makeText (getApplicationContext, msg, Toast.LENGTH_SHORT)
          toast show()
        }
      }

      def sendSuccess(drop: Drop) = {
        val sharedPrefs = PreferenceManager getDefaultSharedPreferences this
        val copyUrl = sharedPrefs getBoolean ("copy_url", true)
        if (copyUrl) {
          val clipboard = getSystemService(Context.CLIPBOARD_SERVICE).asInstanceOf[ClipboardManager]
          clipboard setText (drop.url)
        }
        dropManager.insert(drop)

        if (drop.itemType != ItemType.Bookmark)
          handler post { () ⇒
            val msg = "Item uploaded successfully to CloudApp." + (if (copyUrl) " The URL is copied to the clipboard." else "")
            val toast = Toast makeText (getApplicationContext, msg, Toast.LENGTH_SHORT)
            toast show ()
          }

        logger debug ("New CloudAppItem created '%d'." format drop.id)
      }

    (accountManager getAccountsByType (AccountType)) headOption match {
      case Some(account) ⇒
        val pwd = accountManager blockingGetAuthToken (account, AuthTokenType, true)
        val api = apiFactory create (account.name, pwd)
        Option(intent.getType) collect (handleSendAction(api)) foreach (_ fold (sendFailure(account, pwd) _, sendSuccess _))
      case _ ⇒
        logger.info("no CloudApp account available")
    }
  }

}

object SharingService {
  val UploadDateFormat = new SimpleDateFormat("'Uploaded' yyyy-MM-dd 'at' HH.mm.ss")
  private lazy val Name = classOf[SharingService].getSimpleName
}