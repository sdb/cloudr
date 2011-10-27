package be.ellefant.droid.cloudapp

import android.app.IntentService
import android.content.Intent
import com.cloudapp.impl.CloudAppImpl
import android.accounts.AccountManager
import SharingService._

class SharingService extends IntentService("SharingService") with Logging {
  val tag = Tag

  // TODO: check username and password and show login window if not found
  // catch exception from cloud api
  // show a menu notification? and make it a configuration option
  def onHandleIntent(intent: Intent) = {
    val am = AccountManager.get(this)
    val accounts = am.getAccountsByType(AccountType)
    // TODO how to handle when there are multiple accounts ?
    accounts.headOption match {
      case Some(acc) =>
        val url = intent.getStringExtra(Intent.EXTRA_TEXT)
        val pwd = am.getPassword(acc)
        val api = new CloudAppImpl(acc.name, pwd)
        val bm = api.createBookmark("Test", url)
        logd("new CloudAppItem created %s" format bm.getHref)
      case _ =>
        logw("No CloudApp account found!")
    }
  }

}

object SharingService {
  val Tag = classOf[SharingService].getSimpleName
}