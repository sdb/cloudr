package be.ellefant.droid.cloudapp

import android.accounts.Account
import android.os.Bundle
import android.content.{Context, SyncResult, ContentProviderClient, AbstractThreadedSyncAdapter}
import com.google.inject.Inject

class CloudAppSyncAdapter @Inject protected (context: Context) extends AbstractThreadedSyncAdapter(context, true)
    with Logging {

  def onPerformSync (account: Account, extras: Bundle, authority: String,
                     provider: ContentProviderClient, syncResult: SyncResult) = {
    logger.info("onPerformSync for '%s'" % account.name)
  }

}