package be.ellefant.cloudr

import roboguice.service.RoboService
import android.accounts.{ Account, AccountAuthenticatorResponse, AbstractAccountAuthenticator, AccountManager ⇒ AndroidAccountManager }
import com.google.inject.Inject
import android.os.Bundle
import scalaandroid._

class AuthenticationService extends RoboService
    with Base.CloudrService
    with Service
    with Injection.AccountManager
    with Injection.ApiFactory {

  lazy val authenticator = new Authenticator

  bind {
    case Intent(AndroidAccountManager.ACTION_AUTHENTICATOR_INTENT) ⇒
      authenticator.getIBinder
  }

  protected class Authenticator extends AbstractAccountAuthenticator(AuthenticationService.this) {

    def addAccount(response: AccountAuthenticatorResponse, accountType: String, authTokenType: String,
      requiredFeatures: Array[String], options: Bundle): Bundle = {
      val intent = Intent(AuthenticationService.this, classOf[AuthenticatorActivity])
      intent.putExtra(AuthenticatorActivity.ParamAuthTokenType, authTokenType)
      intent.putExtra(AndroidAccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
      val bundle = new Bundle
      bundle.putParcelable(AndroidAccountManager.KEY_INTENT, intent)
      bundle
    }

    def confirmCredentials(response: AccountAuthenticatorResponse, account: Account, options: Bundle) =
      Option(options) match {
        case Some(opts) if opts.containsKey(AndroidAccountManager.KEY_PASSWORD) ⇒
          val password = options.getString(AndroidAccountManager.KEY_PASSWORD)
          val verified = authenticate(account.name, password)
          val result = new Bundle
          result.putBoolean(AndroidAccountManager.KEY_BOOLEAN_RESULT, verified)
          result
        case _ ⇒
          val intent = Intent(AuthenticationService.this, classOf[AuthenticatorActivity])
          intent.putExtra(AuthenticatorActivity.ParamUsername, account.name)
          intent.putExtra(AuthenticatorActivity.ParamConfirmCredentials, true)
          intent.putExtra(AndroidAccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
          val bundle = new Bundle
          bundle.putParcelable(AndroidAccountManager.KEY_INTENT, intent)
          bundle
      }

    def editProperties(response: AccountAuthenticatorResponse, accountType: String) = throw new UnsupportedOperationException

    def getAuthToken(response: AccountAuthenticatorResponse, account: Account, authTokenType: String, loginOptions: Bundle): Bundle = {
      if (!(authTokenType == AuthTokenType)) {
        val result = new Bundle
        result.putString(AndroidAccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType")
        return result
      }
      val password = accountManager.getPassword(account)
      if (password != null) {
        val verified = authenticate(account.name, password)
        if (verified) {
          val result = new Bundle
          result.putString(AndroidAccountManager.KEY_ACCOUNT_NAME, account.name)
          result.putString(AndroidAccountManager.KEY_ACCOUNT_TYPE, AccountType)
          result.putString(AndroidAccountManager.KEY_AUTHTOKEN, password)
          return result
        }
      }
      val intent = Intent(AuthenticationService.this, classOf[AuthenticatorActivity])
      intent.putExtra(AuthenticatorActivity.ParamUsername, account.name)
      intent.putExtra(AuthenticatorActivity.ParamAuthTokenType, authTokenType)
      intent.putExtra(AndroidAccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
      val bundle = new Bundle
      bundle.putParcelable(AndroidAccountManager.KEY_INTENT, intent)
      bundle
    }

    def getAuthTokenLabel(authTokenType: String): String = authTokenType match {
      case AuthTokenType ⇒ AuthenticationService.this.getString(R.string.cloudapp_label)
      case _             ⇒ null
    }

    def hasFeatures(response: AccountAuthenticatorResponse, account: Account, features: Array[String]): Bundle = {
      val result = new Bundle
      result.putBoolean(AndroidAccountManager.KEY_BOOLEAN_RESULT, false)
      result
    }

    def updateCredentials(response: AccountAuthenticatorResponse, account: Account, authTokenType: String, loginOptions: Bundle): Bundle = {
      val intent = Intent(AuthenticationService.this, classOf[AuthenticatorActivity])
      intent.putExtra(AuthenticatorActivity.ParamUsername, account.name)
      intent.putExtra(AuthenticatorActivity.ParamAuthTokenType, authTokenType)
      intent.putExtra(AuthenticatorActivity.ParamConfirmCredentials, false)
      intent.putExtra(AndroidAccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
      val bundle = new Bundle
      bundle.putParcelable(AndroidAccountManager.KEY_INTENT, intent)
      bundle
    }

    protected def authenticate(username: String, password: String) = (apiFactory create (username, password)).accountDetails().isRight
  }
}