package be.ellefant.droid.cloudapp

import android.os.Build.VERSION
import android.content.{ Context, AbstractThreadedSyncAdapter }
import android.accounts.{ AccountManager, AbstractAccountAuthenticator }
import roboguice.inject.ContextScoped
import com.google.inject.{ Inject, Provider, AbstractModule }
import CloudrModule._

class CloudrModule extends AbstractModule {

  def configure() = {
    if (VERSION.SDK_INT >= 5) {
      bind(classOf[AccountManager]).toProvider(classOf[AccountManagerProvider])
    }
    bind(classOf[ThreadUtil]).toInstance(new ThreadUtil)
    bind(classOf[ApiFactory]).toInstance(new ApiFactory)
    bind(classOf[CloudAppManager]).toInstance(new CloudAppManager)
    bind(classOf[AbstractAccountAuthenticator]).to(classOf[AuthenticationService.Authenticator])
    bind(classOf[AbstractThreadedSyncAdapter]).to(classOf[SyncService.CloudAppSyncAdapter])
  }

}

object CloudrModule {

  @ContextScoped
  class AccountManagerProvider extends Provider[AccountManager] {
    @Inject protected var context: Context = _
    def get(): AccountManager = android.accounts.AccountManager.get(context)
  }

}