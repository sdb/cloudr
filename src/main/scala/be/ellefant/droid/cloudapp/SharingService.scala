package be.ellefant.droid.cloudapp

import android.app.IntentService
import android.content.Intent
import SharingService._

class SharingService extends IntentService("SharingService") with Logging {
  val tag = Tag

  def onHandleIntent(intent: Intent) = {}

}

object SharingService {
  val Tag = classOf[SharingService].getSimpleName
}