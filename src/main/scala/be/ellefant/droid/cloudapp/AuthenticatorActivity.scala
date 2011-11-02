package be.ellefant.droid.cloudapp

import android.accounts.{ Account, AccountManager => AndroidAccountManager }
import android.content.{ DialogInterface, Intent }
import android.os.{ Bundle, Handler }
import android.text.TextUtils
import android.view.{ View, Window }
import android.app.{ Activity, ProgressDialog }
import AuthenticatorActivity._
import android.widget.{Toast, EditText, TextView}
import roboguice.activity.RoboAccountAuthenticatorActivity

/**
 * Activity which displays login screen to the user.
 */
class AuthenticatorActivity extends RoboAccountAuthenticatorActivity
    with Base.Activity
    with Injection.AccountManager {

  private var authThread: Thread = null
  private var authtoken: String = null
  private var authtokenType: String = null
  private var confirmCredentials: Boolean = false
  private var message: TextView = null
  private var password: String = null
  private var passwordEdit: EditText = null
  private var requestNewAccount: Boolean = false
  private var username: String = null
  private var usernameEdit: EditText = null
  private val handler: Handler = new Handler

  override def onCreate(icicle: Bundle) {
    super.onCreate(icicle)
    logger.info("onCreate(" + icicle + ")")
    val accounts = accountManager.getAccountsByType(AccountType)
    if (accounts.size == 0) {
      logger.info("loading data from Intent")
      val intent = getIntent
      username = intent.getStringExtra(ParamUsername)
      authtokenType = intent.getStringExtra(ParamAuthTokenType)
      requestNewAccount = username == null
      confirmCredentials = intent.getBooleanExtra(ParamConfirmCredentials, false)
      logger.info("request new: " + requestNewAccount)
      requestWindowFeature(Window.FEATURE_LEFT_ICON)
      setContentView(R.layout.login_activity)
      getWindow.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, android.R.drawable.ic_dialog_alert)
      message = findViewById(R.id.message).asInstanceOf[TextView]
      usernameEdit = findViewById(R.id.username_edit).asInstanceOf[EditText]
      passwordEdit = findViewById(R.id.password_edit).asInstanceOf[EditText]
      usernameEdit.setText(username)
      message.setText(getMessage)
    } else {
      val toast = Toast.makeText(getApplicationContext, "Only one CloudApp account is supported.", Toast.LENGTH_SHORT)
      toast.show()
      finish()
    }
  }

  protected override def onCreateDialog(id: Int) = {
    val dialog = new ProgressDialog(this)
    dialog.setMessage(getText(R.string.ui_activity_authenticating))
    dialog.setIndeterminate(true)
    dialog.setCancelable(true)
    dialog.setOnCancelListener(new DialogInterface.OnCancelListener {
      def onCancel(dialog: DialogInterface) {
        logger.info("dialog cancel has been invoked")
        if (authThread != null) {
          authThread.interrupt
          finish
        }
      }
    })
    dialog
  }

  def handleLogin(view: View) {
    if (requestNewAccount) {
      username = usernameEdit.getText.toString
    }
    password = passwordEdit.getText.toString
    if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
      message.setText(getMessage)
    }
    else {
      showProgress
      authThread = CloudApi.attemptAuth(username, password, handler, AuthenticatorActivity.this)
    }
  }

  private def finishConfirmCredentials(result: Boolean) {
    logger.info("finishConfirmCredentials()")
    val account = new Account(username, AccountType)
    accountManager.setPassword(account, password)
    val intent = new Intent
    intent.putExtra(AndroidAccountManager.KEY_BOOLEAN_RESULT, result)
    setAccountAuthenticatorResult(intent.getExtras)
    setResult(Activity.RESULT_OK, intent)
    finish
  }

  private def finishLogin {
    logger.info("finishLogin()")
    val account = new Account(username, AccountType)
    if (requestNewAccount) {
      accountManager.addAccountExplicitly(account, password)
      // ContentResolver.setSyncAutomatically(account, ContactsContract.AUTHORITY, true)
    }
    else {
      accountManager.setPassword(account, password)
    }
    val intent = new Intent
    authtoken = password
    intent.putExtra(AndroidAccountManager.KEY_ACCOUNT_NAME, username)
    intent.putExtra(AndroidAccountManager.KEY_ACCOUNT_TYPE, AccountType)
    if (authtokenType != null && (authtokenType == AuthTokenType)) {
      intent.putExtra(AndroidAccountManager.KEY_AUTHTOKEN, authtoken)
    }
    setAccountAuthenticatorResult(intent.getExtras)
    setResult(Activity.RESULT_OK, intent)
    finish
  }

  private def hideProgress {
    dismissDialog(0)
  }

  def onAuthenticationResult(result: Boolean) {
    logger.info("onAuthenticationResult(" + result + ")")
    hideProgress
    if (result) {
      if (!confirmCredentials) {
        finishLogin
      }
      else {
        finishConfirmCredentials(true)
      }
    }
    else {
      logger.info("onAuthenticationResult: failed to authenticate")
      if (requestNewAccount) {
        message.setText(getText(R.string.login_activity_loginfail_text_both))
      }
      else {
        message.setText(getText(R.string.login_activity_loginfail_text_pwonly))
      }
    }
  }

  private def getMessage: CharSequence = {
    getString(R.string.cloudapp_label)
    if (TextUtils.isEmpty(username)) {
      val msg: CharSequence = getText(R.string.login_activity_newaccount_text)
      return msg
    }
    if (TextUtils.isEmpty(password)) {
      return getText(R.string.login_activity_loginfail_text_pwmissing)
    }
    return null
  }

  private def showProgress {
    showDialog(0)
  }
}

object AuthenticatorActivity {
  val ParamAuthTokenType = "authtokenType"
  val ParamUsername = "username"
  val ParamPassword = "password"
  val ParamConfirmCredentials = "confirmCredentials"
}