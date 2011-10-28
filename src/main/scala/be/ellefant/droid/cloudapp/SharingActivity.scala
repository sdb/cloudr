package be.ellefant.droid.cloudapp

import android.widget.Toast
import android.app.Activity
import android.content.Intent
import android.os.Bundle

class SharingActivity extends Activity with Logging with AccountRequired {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
		val intent = getIntent
    (intent.getAction, intent.getType) match {
      case (Intent.ACTION_SEND, "text/plain") =>
        withAccount(onSuccess _, onFailure _)
      case (a, t) =>
        logw("Action %s and type %s not supported" format (a, t))
    }
    finish()
  }

  private def onSuccess(name: String) {
    val intent = getIntent
    val url = intent.getStringExtra(Intent.EXTRA_TEXT)
    logd("sharing link %s" format url)
    val toast = Toast.makeText(getApplicationContext, "URL will be saved to CloudApp", Toast.LENGTH_SHORT)
    toast.show()
    val int = new Intent(intent)
    int.setClass(this, classOf[SharingService])
    startService(int)
  }

  private def onFailure() {
    val toast = Toast.makeText(getApplicationContext, "URL couldn't be saved to CloudApp", Toast.LENGTH_SHORT)
    toast.show()
  }
}