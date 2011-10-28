package be.ellefant.droid.cloudapp

import android.content.{Intent, Context}
import android.os.Bundle
import android.accounts.{Account, AccountAuthenticatorResponse, AccountManager, AbstractAccountAuthenticator}

class CloudAppAuthenticator(context: Context) extends AbstractAccountAuthenticator(context) {

  def addAccount(response: AccountAuthenticatorResponse, accountType: String, authTokenType: String,
                 requiredFeatures: Array[String], options: Bundle): Bundle = {
    val intent = new Intent(context, classOf[AuthenticatorActivity])
    intent.putExtra(AuthenticatorActivity.ParamAuthTokenType, authTokenType)
    intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
    val bundle = new Bundle
    bundle.putParcelable(AccountManager.KEY_INTENT, intent)
    bundle
  }

  def confirmCredentials(response: AccountAuthenticatorResponse, account: Account, options: Bundle) =
    Option(options) match {
      case Some(opts) if opts.containsKey(AccountManager.KEY_PASSWORD) =>
        val password = options.getString(AccountManager.KEY_PASSWORD)
        val verified = CloudApi.authenticate(account.name, password, null, null)
        val result = new Bundle
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, verified)
        result
      case _ =>
        val intent = new Intent(context, classOf[AuthenticatorActivity])
        intent.putExtra(AuthenticatorActivity.ParamUsername, account.name)
        intent.putExtra(AuthenticatorActivity.ParamConfirmCredentials, true)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
        val bundle = new Bundle
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)
        bundle
    }

  def editProperties(response: AccountAuthenticatorResponse, accountType: String) = throw new UnsupportedOperationException

  def getAuthToken(response: AccountAuthenticatorResponse, account: Account, authTokenType: String, loginOptions: Bundle): Bundle = {
    if (!(authTokenType == AuthTokenType)) {
      val result = new Bundle
      result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType")
      return result
    }
    val am = AccountManager.get(context)
    val password: String = am.getPassword(account)
    if (password != null) {
      val verified = CloudApi.authenticate(account.name, password, null, null)
      if (verified) {
        val result = new Bundle
        result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, AccountType)
        result.putString(AccountManager.KEY_AUTHTOKEN, password)
        return result
      }
    }
    val intent = new Intent(context, classOf[AuthenticatorActivity])
    intent.putExtra(AuthenticatorActivity.ParamUsername, account.name)
    intent.putExtra(AuthenticatorActivity.ParamAuthTokenType, authTokenType)
    intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
    val bundle = new Bundle
    bundle.putParcelable(AccountManager.KEY_INTENT, intent)
    bundle
  }

  def getAuthTokenLabel(authTokenType: String): String = authTokenType match {
    case AuthTokenType => context.getString(R.string.cloudapp_label)
    case _ => null
  }

  def hasFeatures(response: AccountAuthenticatorResponse, account: Account, features: Array[String]): Bundle = {
    val result = new Bundle
    result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false)
    result
  }

  def updateCredentials(response: AccountAuthenticatorResponse, account: Account, authTokenType: String, loginOptions: Bundle): Bundle = {
    val intent = new Intent(context, classOf[AuthenticatorActivity])
    intent.putExtra(AuthenticatorActivity.ParamUsername, account.name)
    intent.putExtra(AuthenticatorActivity.ParamAuthTokenType, authTokenType)
    intent.putExtra(AuthenticatorActivity.ParamConfirmCredentials, false)
    val bundle: Bundle = new Bundle
    bundle.putParcelable(AccountManager.KEY_INTENT, intent)
    bundle
  }

}