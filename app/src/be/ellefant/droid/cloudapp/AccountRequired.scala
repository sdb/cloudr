package be.ellefant.droid.cloudapp

import android.os.Bundle
import ThreadUtils._
import android.app.Activity

trait AccountRequired extends Activity
  with Injection.AccountManager
  with Injection.ThreadUtil { self: Activity with Logging =>

  protected def onAccountSuccess(name: String): Any
  protected def onAccountFailure(): Any

  abstract override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    val accounts = accountManager.getAccountsByType(AccountType)
    if (accounts.size == 0) {
      logger.debug("Adding account before continuing.")
      val amf = accountManager.addAccount(AccountType, AuthTokenType, this)
      threadUtil.performOnBackgroundThread { () =>
        try {
          val b = amf.getResult // TODO: use timeout
          val name = b.getString(android.accounts.AccountManager.KEY_ACCOUNT_NAME)
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
      logger.debug("Account found: '%s'." format name)
      onAccountSuccess(name)
    }
  }

}