package be.ellefant.droid.cloudapp

import com.google.inject.AbstractModule
import android.os.Build.VERSION

class CloudAppModule extends AbstractModule {

  def configure() = {
    if (VERSION.SDK_INT >= 5) {
      bind(classOf[AccountManager]).toProvider(classOf[AccountManagerProvider])
    }
    bind(classOf[ThreadUtil]).toInstance(new ThreadUtilImpl)
    bind(classOf[ApiFactory]).toInstance(new ApiFactoryImpl)
    bind(classOf[CloudAppManager]).toInstance(new CloudAppManager)
  }

}