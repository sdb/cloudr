package be.ellefant.cloudr

import android.app.Activity
import android.os.Handler
import android.database.ContentObserver

trait DropsContentObservingActivity extends Activity {

  private var observer: DropsContentObserver = _

  private var handler: Handler = _

  protected def initObserver = {
    handler = new Handler
    registerObserver()
  }

  private def registerObserver() = {
    observer = new DropsContentObserver(handler)
    getContentResolver.registerContentObserver(CloudAppProvider.ContentUri, true, observer)
  }

  override protected def onStart = {
    super.onStart()
    registerObserver()
  }

  override protected def onStop = {
    getContentResolver.unregisterContentObserver(observer)
    observer = null
    super.onStop()
  }
  
  protected def onDropsChanges

  private class DropsContentObserver(handler: Handler) extends ContentObserver(handler) {
    override def onChange(selfChange: Boolean) {
      onDropsChanges
    }
  }

}