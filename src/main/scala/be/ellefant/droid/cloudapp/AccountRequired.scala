package be.ellefant.droid.cloudapp

import android.app.Activity
import android.accounts.AccountManager
import ThreadUtils._
import android.os.Bundle

trait AccountRequired extends Activity {
  self: Activity with Logging =>

  protected def onAccountSuccess(name: String): Any
  protected def onAccountFailure(): Any

  abstract override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    val am = AccountManager.get(this)
    val accounts = am.getAccountsByType(AccountType)
    if (accounts.size == 0) {
      val amf = am.addAccount(AccountType, AuthTokenType, null, null, this, null, null)
      performOnBackgroundThread { () =>
        try {
          val b = amf.getResult // wait for result from account creation
          self.runOnUiThread { () =>
            onAccountSuccess(b.getString(AccountManager.KEY_ACCOUNT_NAME))
          }
        } catch {
          case e => // TODO handle other cases then wrong login/password: server unavailable, ...
            logd("failed to add account", e)
            self.runOnUiThread { () =>
              onAccountFailure()
            }
        }
      }
    } else {
      onAccountSuccess(accounts.head.name)
    }
  }

}