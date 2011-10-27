package be.ellefant.droid.cloudapp

import android.app.Service
import android.content.Intent
import android.os.IBinder

import AuthenticationService._

/**
 * Service to handle Account authentication. It instantiates the authenticator
 * and returns its IBinder.
 */
object AuthenticationService extends Logging {
  protected lazy val tag = classOf[AuthenticationService].getName
}

class AuthenticationService extends Service {
  override def onCreate: Unit = {
    logd("SampleSyncAdapter Authentication Service started.")
    mAuthenticator = new CloudAppAuthenticator(this)
  }

  override def onDestroy: Unit = {
    logd("SampleSyncAdapter Authentication Service stopped.")
  }

  def onBind(intent: Intent): IBinder = {
    logd("getBinder()...  returning the AccountAuthenticator binder for intent " + intent)
    mAuthenticator.getIBinder
  }

  private var mAuthenticator: CloudAppAuthenticator = null
}

