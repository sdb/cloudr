package be.ellefant.droid.cloudapp

import android.accounts.Account
import android.os.Bundle
import com.google.inject.Inject
import android.content._

class CloudAppSyncAdapter @Inject protected (context: Context) extends AbstractThreadedSyncAdapter(context, true)
    with Logging
    with Injection.AccountManager {

  def onPerformSync (account: Account, extras: Bundle, authority: String,
                     provider: ContentProviderClient, syncResult: SyncResult) {
    logger.info("onPerformSync for '%s'" % account.name)
    extras match {
      case b if shouldProcessRequest(b) =>
        logger.info("Processing sync with extras " + extras)
        val p = provider.getLocalContentProvider.asInstanceOf[CloudAppProvider]
        Option(accountManager.getPassword(account)) match {
          case Some(pwd) =>
            // TODO get data
          case _ =>
            logger.info("No password available")
            // TODO
        }
      case _ =>
        logger.info("Didn't process sync with extras " + extras)
    }
  }

  protected def shouldProcessRequest(bundle: Bundle) = {
    (bundle != null &&
      (bundle.containsKey(ContentResolver.SYNC_EXTRAS_INITIALIZE) ||
        bundle.containsKey(ContentResolver.SYNC_EXTRAS_MANUAL)))
  }

}