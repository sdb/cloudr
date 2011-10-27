package be.ellefant.droid.cloudapp

import android.os.Bundle
import android.widget.Toast
import SharingActivity._
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent

/**
 * Activity for sharing/posting a link (bookmark) to CloudApp.
 */
class SharingActivity extends BaseActivity {
  val tag =  Tag

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
		val intent = getIntent
    (intent.getAction, intent.getType) match {
      case (Intent.ACTION_SEND, "text/plain") =>
        val am = AccountManager.get(this)
        val accounts = am.getAccountsByType(AccountType)
        if (accounts.size == 0) {
          startActivityForResult(new Intent(this, classOf[AuthenticatorActivity]), 0)
        } else {
          share()
          finish()
        }
      case (a, t) =>
        logw("action %s and type %s not supported" format (a, t))
    }
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

  override def onActivityResult(requestCode: Int, resultCode: Int, data: Intent) = {
    if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
      share()
    } else {
      val toast = Toast.makeText(getApplicationContext, "URL couldn't be saved to CloudApp", Toast.LENGTH_SHORT)
      toast.show()
    }
    finish()
  }
}

object SharingActivity {
  val Tag = classOf[SharingActivity].getSimpleName
}