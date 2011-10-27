package be.ellefant.droid.cloudapp

import android.os.Handler
import android.content.Context

import CloudApi._
import com.cloudapp.impl.CloudAppImpl

object CloudApi {
  def authenticate(username: String, password: String, handler: Handler, context: Context) = {
    try {
      new CloudAppImpl(username, password).getAccountDetails
      sendResult(true, handler, context)
      true
    } catch {
      case e =>
        sendResult(false, handler, context)
        false
    }
  }

  private def sendResult(result: Boolean, handler: Handler, context: Context): Unit = {
    if (handler == null || context == null) {
      return
    }
    handler.post(new Runnable {
      def run: Unit = {
        (context.asInstanceOf[AuthenticatorActivity]).onAuthenticationResult(result)
      }
    })
  }

  def attemptAuth(username: String, password: String, handler: Handler, context: Context): Thread = {
    val runnable: Runnable = new Runnable {
      def run: Unit = {
        authenticate(username, password, handler, context)
      }
    }
    performOnBackgroundThread(runnable)
  }

  def performOnBackgroundThread(runnable: Runnable): Thread = {
    val t: Thread = new Thread {
      override def run: Unit = {
        try {
          runnable.run
        }
        finally {
        }
      }
    }
    t.start
    return t
  }
}