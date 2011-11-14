package be.ellefant.droid.cloudapp

import android.content.Intent
import roboguice.service.RoboIntentService
import SharingService._
import android.text.ClipboardManager
import android.content.Context
import android.preference.PreferenceManager

class SharingService extends RoboIntentService(Name)
    with Base.Service
    with Injection.AccountManager
    with Injection.ApiFactory {

  def onHandleIntent(intent: Intent) = {
    accountManager.getAccountsByType(AccountType).headOption match {
      case Some(acc) ⇒
        val url = intent.getStringExtra(Intent.EXTRA_TEXT) // TODO handle extras
        val title = intent.getStringExtra(Intent.EXTRA_SUBJECT)
        val pwd = accountManager.getPassword(acc)
        try {
          val api = apiFactory.create(acc.name, pwd)
          val bm = api.createBookmark(title, url)
          val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)
          val copy = sharedPrefs.getBoolean("copy_url", true)
          if (copy) {
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE).asInstanceOf[ClipboardManager]
            clipboard.setText(bm.getUrl())
          }
          logger.debug("New CloudAppItem created '%d'." format bm.getId())
        } catch {
          case e ⇒
            logger.warn("Failed to create new CloudAppItem for '%s'." format url) // TODO catch specific exceptions and show toast message
        }
      case _ ⇒
        logger.warn("No CloudApp account found.")
    }
  }

}

object SharingService {
  private lazy val Name = classOf[SharingService].getSimpleName
}