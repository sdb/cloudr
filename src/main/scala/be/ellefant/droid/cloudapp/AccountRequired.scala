package be.ellefant.droid.cloudapp

import android.app.Activity
import android.accounts.AccountManager
import ThreadUtils._

trait AccountRequired {
  self: Activity with Logging =>

  protected def withAccount(success: (String) => Unit, failure: () => Unit) {
    val am = AccountManager.get(this)
    val accounts = am.getAccountsByType(AccountType)
    if (accounts.size == 0) {
      val amf = am.addAccount(AccountType, AuthTokenType, null, null, this, null, null)
      performOnBackgroundThread { () =>
        try {
          val b = amf.getResult // wait for result from account creation
          self.runOnUiThread {
            success(b.getString(AccountManager.KEY_ACCOUNT_NAME))
          }
        } catch {
          case e => // TODO handle other cases then wrong login/password: server unavailable, ...
            logd("failed to add account", e)
            self.runOnUiThread {
              failure()
            }
        }
      }
    } else {
      success(accounts.head.name)
    }
  }

}