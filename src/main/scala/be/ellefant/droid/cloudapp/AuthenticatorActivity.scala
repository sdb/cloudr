package be.ellefant.droid.cloudapp

import android.accounts.Account
import android.accounts.AccountAuthenticatorActivity
import android.accounts.AccountManager
import android.content.ContentResolver
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.provider.ContactsContract
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.TextView
import android.app.{Activity, Dialog, ProgressDialog}

import AuthenticatorActivity._

/**
 * Activity which displays login screen to the user.
 */
object AuthenticatorActivity extends Logging {
  protected lazy val tag = classOf[AuthenticatorActivity].getName
  val ParamAuthTokenType = "authtokenType"
  val ParamUsername = "username"
  val ParamPassword = "password"
  val ParamConfirmCredentials = "confirmCredentials"
}

class AuthenticatorActivity extends AccountAuthenticatorActivity {

  override def onCreate(icicle: Bundle): Unit = {
    logi("onCreate(" + icicle + ")")
    super.onCreate(icicle)
    mAccountManager = AccountManager.get(this)
    logi("loading data from Intent")
    val intent = getIntent
    mUsername = intent.getStringExtra(ParamUsername)
    mAuthtokenType = intent.getStringExtra(ParamAuthTokenType)
    mRequestNewAccount = mUsername == null
    mConfirmCredentials = intent.getBooleanExtra(ParamConfirmCredentials, false)
    logi("    request new: " + mRequestNewAccount)
    requestWindowFeature(Window.FEATURE_LEFT_ICON)
    setContentView(R.layout.login_activity)
    getWindow.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_alert)
    mMessage = findViewById(R.id.message).asInstanceOf[TextView]
    mUsernameEdit = findViewById(R.id.username_edit).asInstanceOf[EditText]
    mPasswordEdit = findViewById(R.id.password_edit).asInstanceOf[EditText]
    mUsernameEdit.setText(mUsername)
    mMessage.setText(getMessage)
  }

  protected override def onCreateDialog(id: Int): Dialog = {
    val dialog = new ProgressDialog(this)
    dialog.setMessage(getText(R.string.ui_activity_authenticating))
    dialog.setIndeterminate(true)
    dialog.setCancelable(true)
    dialog.setOnCancelListener(new DialogInterface.OnCancelListener {
      def onCancel(dialog: DialogInterface): Unit = {
        logi("dialog cancel has been invoked")
        if (mAuthThread != null) {
          mAuthThread.interrupt
          finish
        }
      }
    })
    return dialog
  }

  /**
   * Handles onClick event on the Submit button. Sends username/password to
   * the server for authentication.
   *
   * @param view The Submit button for which this method is invoked
   */
  def handleLogin(view: View): Unit = {
    if (mRequestNewAccount) {
      mUsername = mUsernameEdit.getText.toString
    }
    mPassword = mPasswordEdit.getText.toString
    if (TextUtils.isEmpty(mUsername) || TextUtils.isEmpty(mPassword)) {
      mMessage.setText(getMessage)
    }
    else {
      showProgress
      mAuthThread = CloudApi.attemptAuth(mUsername, mPassword, mHandler, AuthenticatorActivity.this)
    }
  }

  /**
   * Called when response is received from the server for confirm credentials
   * request. See onAuthenticationResult(). Sets the
   * AccountAuthenticatorResult which is sent back to the caller.
   *
   * @param the confirmCredentials result.
   */
  private def finishConfirmCredentials(result: Boolean): Unit = {
    logi("finishConfirmCredentials()")
    val account = new Account(mUsername, AccountType)
    mAccountManager.setPassword(account, mPassword)
    val intent = new Intent
    intent.putExtra(AccountManager.KEY_BOOLEAN_RESULT, result)
    setAccountAuthenticatorResult(intent.getExtras)
    setResult(Activity.RESULT_OK, intent)
    finish
  }

  /**
   * Called when response is received from the server for authentication
   * request. See onAuthenticationResult(). Sets the
   * AccountAuthenticatorResult which is sent back to the caller. Also sets
   * the authToken in AccountManager for this account.
   *
   * @param the confirmCredentials result.
   */
  private def finishLogin: Unit = {
    logi("finishLogin()")
    val account: Account = new Account(mUsername, AccountType)
    if (mRequestNewAccount) {
      mAccountManager.addAccountExplicitly(account, mPassword, null)
      // ContentResolver.setSyncAutomatically(account, ContactsContract.AUTHORITY, true)
    }
    else {
      mAccountManager.setPassword(account, mPassword)
    }
    val intent: Intent = new Intent
    mAuthtoken = mPassword
    intent.putExtra(AccountManager.KEY_ACCOUNT_NAME, mUsername)
    intent.putExtra(AccountManager.KEY_ACCOUNT_TYPE, AccountType)
    if (mAuthtokenType != null && (mAuthtokenType == AuthTokenType)) {
      intent.putExtra(AccountManager.KEY_AUTHTOKEN, mAuthtoken)
    }
    setAccountAuthenticatorResult(intent.getExtras)
    setResult(Activity.RESULT_OK, intent)
    finish
  }

  /**
   * Hides the progress UI for a lengthy operation.
   */
  private def hideProgress: Unit = {
    dismissDialog(0)
  }

  /**
   * Called when the authentication process completes (see attemptLogin()).
   */
  def onAuthenticationResult(result: Boolean): Unit = {
    logi("onAuthenticationResult(" + result + ")")
    hideProgress
    if (result) {
      if (!mConfirmCredentials) {
        finishLogin
      }
      else {
        finishConfirmCredentials(true)
      }
    }
    else {
      loge("onAuthenticationResult: failed to authenticate")
      if (mRequestNewAccount) {
        mMessage.setText(getText(R.string.login_activity_loginfail_text_both))
      }
      else {
        mMessage.setText(getText(R.string.login_activity_loginfail_text_pwonly))
      }
    }
  }

  /**
   * Returns the message to be displayed at the top of the login dialog box.
   */
  private def getMessage: CharSequence = {
    getString(R.string.cloudapp_label)
    if (TextUtils.isEmpty(mUsername)) {
      val msg: CharSequence = getText(R.string.login_activity_newaccount_text)
      return msg
    }
    if (TextUtils.isEmpty(mPassword)) {
      return getText(R.string.login_activity_loginfail_text_pwmissing)
    }
    return null
  }

  /**
   * Shows the progress UI for a lengthy operation.
   */
  private def showProgress: Unit = {
    showDialog(0)
  }

  private var mAccountManager: AccountManager = null
  private var mAuthThread: Thread = null
  private var mAuthtoken: String = null
  private var mAuthtokenType: String = null
  /**
   * If set we are just checking that the user knows their credentials; this
   * doesn't cause the user's password to be changed on the device.
   */
  private var mConfirmCredentials: Boolean = false
  /**for posting authentication attempts back to UI thread */
  private final val mHandler: Handler = new Handler
  private var mMessage: TextView = null
  private var mPassword: String = null
  private var mPasswordEdit: EditText = null
  /**Was the original caller asking for an entirely new account? */
  protected var mRequestNewAccount: Boolean = false
  private var mUsername: String = null
  private var mUsernameEdit: EditText = null
}