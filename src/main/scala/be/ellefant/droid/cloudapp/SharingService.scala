package be.ellefant.droid.cloudapp

import android.content.Intent
import roboguice.service.RoboIntentService
import com.google.inject.{AbstractModule, Inject}
import SharingService._

class SharingService extends RoboIntentService(Name) with Logging {

  @Inject protected var apiFactory: ApiFactory = _
  @Inject protected var accountManager: AccountManager = _

  def onHandleIntent(intent: Intent) = {
    val accounts = accountManager.getAccountsByType(AccountType)
    accounts.headOption match {
      case Some(acc) =>
        val url = intent.getStringExtra(Intent.EXTRA_TEXT) // TODO handle extras
        val title = intent.getStringExtra(Intent.EXTRA_SUBJECT)
        val pwd = accountManager.getPassword(acc)
        try {
          val api = apiFactory.create(acc.name, pwd)
          val bm = api.createBookmark(title, url)
          logger.debug("New CloudAppItem created '%s'." format bm.getHref)
        } catch {
          case e =>
            logger.warn("Failed to create new CloudAppItem for '%s'." format url)
        }
      case _ =>
        logger.warn("No CloudApp account found.")
    }
  }

}

object SharingService {
  private lazy val Name = classOf[SharingService].getSimpleName
}