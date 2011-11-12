package be.ellefant.droid.cloudapp

import com.google.inject.Inject
import android.accounts.AbstractAccountAuthenticator
import android.content.AbstractThreadedSyncAdapter

object Injection {

  trait AccountManager {
    @Inject protected var accountManager: android.accounts.AccountManager = _
  }

  trait ApiFactory {
    @Inject protected var apiFactory: be.ellefant.droid.cloudapp.ApiFactory = _
  }

  trait ThreadUtil {
    @Inject protected var threadUtil: be.ellefant.droid.cloudapp.ThreadUtil = _
  }

  trait Resources {
    @Inject var resources: android.content.res.Resources = _
  }

  trait CloudAppManager {
    @Inject protected var cloudAppManager: be.ellefant.droid.cloudapp.CloudAppManager = _
  }
}