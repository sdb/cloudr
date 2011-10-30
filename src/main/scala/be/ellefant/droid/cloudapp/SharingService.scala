package be.ellefant.droid.cloudapp

import android.app.IntentService
import android.content.Intent
import android.accounts.{AccountManager => AndroidAccountManager}
import com.cloudapp.impl.CloudAppImpl
import SharingService._

class SharingService extends IntentService(Name) with Logging {

  def onHandleIntent(intent: Intent) = {
    val am = AndroidAccountManager.get(this)
    val accounts = am.getAccountsByType(AccountType)
    accounts.headOption match {
      case Some(acc) =>
        val url = intent.getStringExtra(Intent.EXTRA_TEXT) // TODO handle extras
        val title = intent.getStringExtra(Intent.EXTRA_SUBJECT)
        val pwd = am.getPassword(acc)
        try {
          val api = new CloudAppImpl(acc.name, pwd)
          val bm = api.createBookmark(title, url)
          logd("New CloudAppItem created '%s'." format bm.getHref)
        } catch {
          case e =>
            logw("Failed to create new CloudAppItem for '%s'." format url)
        }
      case _ =>
        logw("No CloudApp account found.")
    }
  }

}

object SharingService {
  private lazy val Name = classOf[SharingService].getSimpleName
}