package be.ellefant.droid.cloudapp

import android.app.Service
import android.content.Intent

class AuthenticationService extends Service with Logging {
  private var authenticator: CloudAppAuthenticator = null

  override def onCreate {
    authenticator = new CloudAppAuthenticator(this)
  }

  def onBind(intent: Intent) = {
    logd("Returning the AccountAuthenticator binder for intent '%s'." format intent)
    authenticator.getIBinder
  }
}

