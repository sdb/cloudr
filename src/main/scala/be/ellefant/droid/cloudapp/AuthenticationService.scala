package be.ellefant.droid.cloudapp

import roboguice.service.RoboService
import android.accounts.{AccountManager => AndroidAccountManager}

class AuthenticationService extends RoboService
    with Base.Service
    with sdroid.Service
    with Injection.AccountAuthenticator {

  def onBind = {
    case intent if intent.getAction == AndroidAccountManager.ACTION_AUTHENTICATOR_INTENT =>
      logger.debug("Returning the AccountAuthenticator binder for intent '%s'." format intent)
      authenticator.getIBinder
  }
}

