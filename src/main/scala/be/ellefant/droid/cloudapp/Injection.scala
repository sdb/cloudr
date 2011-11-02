package be.ellefant.droid.cloudapp

import com.google.inject.Inject

object Injection {

  trait AccountManager {
    @Inject protected var accountManager: be.ellefant.droid.cloudapp.AccountManager = _
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
    @Inject protected var cloudAppManager: be.ellefant.droid.cloudapp.CloudAppManager  = _
  }
}