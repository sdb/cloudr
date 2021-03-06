package be.ellefant.cloudr

import com.google.inject.Inject

object Injection {

  trait AccountManager {
    @Inject protected var accountManager: android.accounts.AccountManager = _
  }

  trait ApiFactory {
    @Inject protected var apiFactory: be.ellefant.cloudr.ApiFactory = _
  }

  trait Resources {
    @Inject var resources: android.content.res.Resources = _
  }

  trait CloudAppManager {
    @Inject protected var cloudAppManager: be.ellefant.cloudr.CloudAppManager = _
  }

  trait DropManager {
    @Inject protected var dropManager: be.ellefant.cloudr.DropManager = _
  }

  trait Config {
    @Inject protected var config: be.ellefant.cloudr.Config = _
  }
}