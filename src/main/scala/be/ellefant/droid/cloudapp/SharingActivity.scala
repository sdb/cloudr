package be.ellefant.droid.cloudapp

import android.widget.Toast
import android.app.Activity
import android.content.Intent
import android.accounts.AccountManager
import android.os.Bundle
import SharingActivity._
import ThreadUtils._

/**
 * Activity for sharing/posting a link (bookmark) to CloudApp.
 */
class SharingActivity extends Activity {

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
		val intent = getIntent
    (intent.getAction, intent.getType) match {
      case (Intent.ACTION_SEND, "text/plain") =>
        val am = AccountManager.get(this)
        val accounts = am.getAccountsByType(AccountType)
        if (accounts.size == 0) {
          val amf = am.addAccount(AccountType, AuthTokenType, null, null, this, null, null)
          performOnBackgroundThread { () =>
            try {
              amf.getResult // wait for result from account creation
              SharingActivity.this.runOnUiThread(new Runnable {
                def run {
                  share()
                }
              })
            } catch {
              case e =>
                logw("error adding account", e)
                SharingActivity.this.runOnUiThread(new Runnable {
                  def run {
                    val toast = Toast.makeText(getApplicationContext, "URL couldn't be saved to CloudApp", Toast.LENGTH_SHORT)
                    toast.show()
                  }
                })
            }
          }
        } else {
          share()
        }
      case (a, t) =>
        logw("action %s and type %s not supported" format (a, t))
    }
    finish()
  }

  protected def share() = {
    val intent = getIntent
    val url = intent.getStringExtra(Intent.EXTRA_TEXT)
    logd("sharing link %s" format url)
    val toast = Toast.makeText(getApplicationContext, "URL will be saved to CloudApp", Toast.LENGTH_SHORT)
    toast.show()
    val int = new Intent(intent)
    int.setClass(this, classOf[SharingService])
    startService(int)
  }
}

object SharingActivity extends Logging {
  protected lazy val tag = classOf[SharingActivity].getName
}