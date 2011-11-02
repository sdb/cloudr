package be.ellefant.droid.cloudapp

import android.app.Service
import android.content.Intent

class AuthenticationService extends Service with Logging {
  private var authenticator: Authenticator = null

  override def onCreate {
    authenticator = new Authenticator(this)
  }

  def onBind(intent: Intent) = {
    logger.debug("Returning the AccountAuthenticator binder for intent '%s'." format intent)
    authenticator.getIBinder
  }
}

