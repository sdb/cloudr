package be.ellefant.droid.cloudapp

import android.os.Bundle
import ThreadUtils._
import android.app.Activity
import android.accounts.Account

trait AccountRequired extends Activity
    with Injection.AccountManager
    with Injection.ThreadUtil { self: Activity with Logging ⇒

  protected def account() = accountManager.getAccountsByType(AccountType).head

  protected def onAccountSuccess(name: String): Any // TODO: remove param name
  protected def onAccountFailure(): Any

  abstract override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    val accounts = accountManager.getAccountsByType(AccountType)
    if (accounts.size == 0) {
      logger.debug("Adding account before continuing.")
      val amf = accountManager.addAccount(AccountType, AuthTokenType, null, null, this, null, null)
      threadUtil.performOnBackgroundThread { () ⇒
        try {
          val b = amf.getResult // TODO: use timeout
          val name = b.getString(android.accounts.AccountManager.KEY_ACCOUNT_NAME)
          self.runOnUiThread { () ⇒
            onAccountSuccess(name)
          }
        } catch {
          case e ⇒ // TODO handle other cases then wrong login/password: server unavailable, ...
            self.runOnUiThread { () ⇒
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