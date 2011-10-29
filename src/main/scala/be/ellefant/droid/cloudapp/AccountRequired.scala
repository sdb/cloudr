package be.ellefant.droid.cloudapp

import android.app.Activity
import android.accounts.AccountManager
import android.os.Bundle
import ThreadUtils._
import com.google.inject.{Inject, Provider}
import roboguice.activity.RoboActivity

trait AccountRequired extends RoboActivity {
  self: RoboActivity with Logging =>

  protected def onAccountSuccess(name: String): Any
  protected def onAccountFailure(): Any

  @Inject
  protected var accountManagerProvider: AccountManagerProvider = _

  abstract override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    val am = accountManagerProvider.get
    val accounts = am.getAccountsByType(AccountType)
    if (accounts.size == 0) {
      logd("Adding account before continuing.")
      val amf = am.addAccount(AccountType, AuthTokenType, null, null, this, null, null)
      performOnBackgroundThread { () =>
        try {
          val b = amf.getResult // TODO: use timeout
          val name = b.getString(AccountManager.KEY_ACCOUNT_NAME)
          self.runOnUiThread { () =>
            onAccountSuccess(name)
          }
        } catch {
          case e => // TODO handle other cases then wrong login/password: server unavailable, ...
            self.runOnUiThread { () =>
              onAccountFailure()
            }
        }
      }
    } else {
      val name = accounts.head.name
      logd("Account found: '%s'." format name)
      onAccountSuccess(name)
    }
  }

}