package be.ellefant.droid.cloudapp

import android.os.Handler
import android.content.Context
import com.cloudapp.impl.CloudAppImpl
import ThreadUtils._

object CloudApi {
  def authenticate(username: String, password: String, handler: Handler, context: Context) = {
    def sendResult(result: Boolean, handler: Handler, context: Context) = {
      if (handler != null && context != null) {
        handler.post {
          (context.asInstanceOf[AuthenticatorActivity]).onAuthenticationResult(result)
        }
      }
      result
    }
    try {
      new CloudAppImpl(username, password).getAccountDetails
      sendResult(true, handler, context)
    } catch {
      case e =>
        sendResult(false, handler, context)
    }
  }

  def attemptAuth(username: String, password: String, handler: Handler, context: Context) = {
    performOnBackgroundThread {
      authenticate(username, password, handler, context)
    }
  }
}