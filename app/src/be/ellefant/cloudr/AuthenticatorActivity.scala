package be.ellefant.cloudr

import android.accounts.{ Account, AccountManager ⇒ AndroidAccountManager }
import android.os.{ Bundle, Handler }
import android.text.TextUtils
import android.view.{ View, Window }
import android.app.{ Activity, ProgressDialog }
import android.widget.{ Toast, EditText, TextView }
import android.content.{ ContentResolver, Context, DialogInterface, Intent }
import roboguice.activity.RoboAccountAuthenticatorActivity
import AuthenticatorActivity._, ThreadUtils._

/**
 * Activity which displays login screen to the user.
 */
class AuthenticatorActivity extends RoboAccountAuthenticatorActivity
    with Base.CloudrActivity
    with Injection.AccountManager
    with Injection.ApiFactory {

  private var authThread: Thread = null
  private var authToken: String = null
  private var authTokenType: String = null
  private var confirmCredentials: Boolean = false
  private var message: TextView = null
  private var password: String = null
  private var passwordEdit: EditText = null
  private var requestNewAccount: Boolean = false
  private var username: String = null
  private var usernameEdit: EditText = null
  private val handler: Handler = new Handler

  override def onPause = {
    super.onPause
    logger debug ("closing activity on pause")
    finish()
  }

  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    val intent = getIntent
    username = intent.getStringExtra(ParamUsername)
    authTokenType = intent.getStringExtra(ParamAuthTokenType)
    requestNewAccount = username == null
    val accounts = accountManager.getAccountsByType(AccountType)
    if (!requestNewAccount || accounts.size == 0) {
      confirmCredentials = intent.getBooleanExtra(ParamConfirmCredentials, false)
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
    } else {
      showProgress
      authThread = attemptAuth(username, password, handler, AuthenticatorActivity.this)
    }
  }

  private def finishConfirmCredentials(result: Boolean) {
    val account = new Account(username, AccountType)
    accountManager.setPassword(account, password)
    val intent = new Intent
    intent.putExtra(AndroidAccountManager.KEY_BOOLEAN_RESULT, result)
    setAccountAuthenticatorResult(intent.getExtras)
    setResult(Activity.RESULT_OK, intent)
    finish
  }

  private def finishLogin {
    val account = new Account(username, AccountType)
    if (requestNewAccount) {
      accountManager.addAccountExplicitly(account, password, null)
      ContentResolver.setSyncAutomatically(account, Authority, true)
    } else {
      accountManager.setPassword(account, password)
    }
    val intent = new Intent
    authToken = password
    intent.putExtra(AndroidAccountManager.KEY_ACCOUNT_NAME, username)
    intent.putExtra(AndroidAccountManager.KEY_ACCOUNT_TYPE, AccountType)
    if (authTokenType != null && (authTokenType == AuthTokenType)) {
      intent.putExtra(AndroidAccountManager.KEY_AUTHTOKEN, authToken)
    }
    setAccountAuthenticatorResult(intent.getExtras)
    setResult(Activity.RESULT_OK, intent)
    finish
  }

  private def hideProgress {
    dismissDialog(0)
  }

  def onAuthenticationResult(result: Boolean) {
    hideProgress
    if (result) {
      if (!confirmCredentials) {
        finishLogin
      } else {
        finishConfirmCredentials(true)
      }
    } else {
      if (requestNewAccount) {
        message.setText(getText(R.string.login_activity_loginfail_text_both))
      } else {
        message.setText(getText(R.string.login_activity_loginfail_text_pwonly))
      }
    }
  }

  private def getMessage: CharSequence = {
    getString(R.string.app_name) // TODO ?
    if (TextUtils.isEmpty(username)) {
      return getText(R.string.login_activity_newaccount_text)
    }
    if (TextUtils.isEmpty(password)) {
      return getText(R.string.login_activity_loginfail_text_pwmissing)
    }
    return null
  }

  private def showProgress {
    showDialog(0)
  }

  protected def authenticate(username: String, password: String, handler: Handler, context: Context) = {
      def sendResult(result: Boolean, handler: Handler, context: Context) = {
        if (handler != null && context != null) {
          handler.post { () ⇒
            (context.asInstanceOf[AuthenticatorActivity]).onAuthenticationResult(result)
          }
        }
        result
      }
    sendResult((apiFactory create (username, password)).accountDetails().isRight, handler, context)
  }

  protected def attemptAuth(username: String, password: String, handler: Handler, context: Context) = {
    performOnBackgroundThread { () ⇒
      authenticate(username, password, handler, context)
    }
  }
}

object AuthenticatorActivity {
  val ParamAuthTokenType = "authtokenType"
  val ParamUsername = "username"
  val ParamPassword = "password"
  val ParamConfirmCredentials = "confirmCredentials"
}