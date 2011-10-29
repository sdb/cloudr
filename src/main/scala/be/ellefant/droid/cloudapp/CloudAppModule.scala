package be.ellefant.droid.cloudapp

import com.google.inject.AbstractModule
import android.os.Build.VERSION
import android.accounts.AccountManager

class CloudAppModule extends AbstractModule {

  def configure() = {
    if (VERSION.SDK_INT >= 5) {
      bind(classOf[AccountManager]).toProvider(classOf[AccountManagerProvider])
    }
  }

}