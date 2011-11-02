package be.ellefant.droid.cloudapp

import android.content.Intent

class AuthenticationService extends Base.Service {
  private var authenticator: Authenticator = null

  override def onCreate {
    authenticator = new Authenticator(this)
  }

  // TODO: returns the result of getIBinder() in the service's onBind(android.content.Intent) when invoked with an intent with action ACTION_AUTHENTICATOR_INTENT.
  def onBind(intent: Intent) = {
    logger.debug("Returning the AccountAuthenticator binder for intent '%s'." format intent)
    authenticator.getIBinder
  }
}

