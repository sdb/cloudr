package be.ellefant.droid.cloudapp

import com.google.inject.AbstractModule
import android.os.Build.VERSION
import android.accounts.AbstractAccountAuthenticator
import android.content.AbstractThreadedSyncAdapter

class CloudrModule extends AbstractModule {

  def configure() = {
    if (VERSION.SDK_INT >= 5) {
      bind(classOf[AccountManager]).toProvider(classOf[AccountManagerProvider])
    }
    bind(classOf[ThreadUtil]).toInstance(new ThreadUtil)
    bind(classOf[ApiFactory]).toInstance(new ApiFactory)
    bind(classOf[CloudAppManager]).toInstance(new CloudAppManager)
    bind(classOf[AbstractAccountAuthenticator]).to(classOf[Authenticator])
    bind(classOf[AbstractThreadedSyncAdapter]).to(classOf[CloudAppSyncAdapter])
  }

}