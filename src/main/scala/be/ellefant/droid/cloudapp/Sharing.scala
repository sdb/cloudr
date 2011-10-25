package be.ellefant.droid.cloudapp

import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.widget.Toast
import Sharing._

/**
 * Activity for sharing/posting a link (bookmark) to CloudApp.
 */
class Sharing extends Activity with Logging {
  val tag = Tag

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
		val intent = getIntent
    (intent.getAction, intent.getType) match {
      case (Intent.ACTION_SEND, "text/plain") =>
        share(intent.getStringExtra(Intent.EXTRA_TEXT))
      case (a, t) =>
        logw("action %s and type %s not supported" format (a, t))
    }
    finish()
  }

  protected def share(url: String) = {
    logd("sharing link %s" format url)
    val toast = Toast.makeText(getApplicationContext, "URL will be saved to CloudApp", Toast.LENGTH_SHORT)
    toast.show()
  }
}

object Sharing {
  val Tag = classOf[Sharing].getSimpleName
}