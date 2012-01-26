package be.ellefant.cloudr

import android.widget.Toast
import android.content.Intent
import android.os.Bundle
import roboguice.activity.RoboActivity

/**
 * This activity handles <code>SEND</code> intents from various sources. The received intent is forwarded to the
 * SharingService if a CloudApp account is registered with the Android account manager.
 */
class SharingActivity extends RoboActivity
    with Base.AccountRequired
    with Injection.CloudAppManager {

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    finish()
  }

  protected[cloudr] def onAccountSuccess() = {
    val intent = getIntent
    val mimeType = intent.getType
    if (Extension.isSupported(mimeType)) {
      val url = intent.getStringExtra(Intent.EXTRA_TEXT)
      logger.debug("Sharing link '%s' for '%s'." format (url, account().name))
      val toast = Toast.makeText(getApplicationContext, "Item will be uploaded to CloudApp.", Toast.LENGTH_SHORT)
      toast.show()
      val int = new Intent(intent)
      int.setClass(this, classOf[SharingService])
      startService(int)
    } else {
      logger warn ("mime type %s is not supported" format mimeType)
      val toast = Toast.makeText(getApplicationContext, "Can't upload item to CloudApp. This kind of item is not supported.", Toast.LENGTH_SHORT)
      toast.show()
    }
  }

  override protected[cloudr] def onAccountFailure() = {
    logger.debug("No account to share link.")
    val toast = Toast.makeText(getApplicationContext, "This action requires a CloudApp account", Toast.LENGTH_SHORT)
    toast.show()
  }
}