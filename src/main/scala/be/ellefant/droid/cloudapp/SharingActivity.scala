package be.ellefant.droid.cloudapp

import android.widget.Toast
import android.app.Activity
import android.content.Intent
import android.os.Bundle

class SharingActivity extends Activity with Logging with AccountRequired {

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    finish()
  }

  protected def onAccountSuccess(name: String) = {
    val intent = getIntent
    val url = intent.getStringExtra(Intent.EXTRA_TEXT)
    logd("sharing link %s" format url)
    val toast = Toast.makeText(getApplicationContext, "URL will be saved to CloudApp", Toast.LENGTH_SHORT)
    toast.show()
    val int = new Intent(intent)
    int.setClass(this, classOf[SharingService])
    startService(int)
  }

  protected def onAccountFailure() = {
    val toast = Toast.makeText(getApplicationContext, "URL couldn't be saved to CloudApp", Toast.LENGTH_SHORT)
    toast.show()
  }
}