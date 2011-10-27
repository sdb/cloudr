package be.ellefant.droid.cloudapp

import android.app.Service
import android.content.Intent

import AuthenticationService._

/**
 * Service to handle Account authentication. It instantiates the authenticator
 * and returns its IBinder.
 */
object AuthenticationService extends Logging {
  protected lazy val tag = classOf[AuthenticationService].getName
}

class AuthenticationService extends Service {
  private var authenticator: CloudAppAuthenticator = null

  override def onCreate {
    logd("SampleSyncAdapter Authentication Service started.")
    authenticator = new CloudAppAuthenticator(this)
  }

  override def onDestroy {
    logd("SampleSyncAdapter Authentication Service stopped.")
  }

  def onBind(intent: Intent) = {
    logd("getBinder()...  returning the AccountAuthenticator binder for intent " + intent)
    authenticator.getIBinder
  }
}

