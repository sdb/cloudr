package be.ellefant.cloudr

import android.os.Bundle
import android.app.Activity
import android.accounts.AccountManagerFuture

// TODO: use functions => withAccount, noAccount
trait AccountRequired extends Activity
    with Injection.AccountManager { self: Activity with Logging ⇒

  protected def account() = accountManager.getAccountsByType(AccountType).head

  protected def onAccountSuccess(): Any
  protected def onAccountFailure(): Any

  abstract override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    val accounts = accountManager.getAccountsByType(AccountType)
    if (accounts.size == 0) {
      logger.debug("Adding account before continuing.")
      val amf = accountManager.addAccount(AccountType, AuthTokenType, null, null, this, null, null)
      handleAccountManagerResult(amf)
    } else {
      val acc = accounts.head
      if (accountManager.getPassword(acc) == null) { // check if the account password needs to be updated
        logger.debug("Updating password before continuing.")
        val amf = accountManager.updateCredentials(acc, AuthTokenType, null, this, null, null)
        handleAccountManagerResult(amf)
      } else {
        val name = acc.name
        logger.debug("Account found: '%s'." format name)
        onAccountSuccess()
      }
    }
  }

  def handleAccountManagerResult(amf: AccountManagerFuture[_]) = {
    val task = new ScalaAsyncTask[Void, Void, Boolean] {
      def doInBackground = {
        try {
          amf.getResult // TODO: use timeout ?
          true
        } catch {
          case e ⇒ // TODO handle other cases than wrong login/password: server unavailable, ...
            logger error ("error while authenticating", e)
            false
        }
      }
      override def onPostExecute(ok: Boolean) = if (ok) onAccountSuccess() else onAccountFailure()
    }
    task.execute()
  }

}