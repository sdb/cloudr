package sdroid

import Types._
import android.content.Intent
import android.os.IBinder

trait Service {
  self: AService =>

  def onBind(intent: Intent): IBinder = (onBind orElse unsupported)(intent)

  def onBind: PartialFunction[Intent, IBinder]

  def unsupported: PartialFunction[Intent, IBinder] = {
    case _ => null
  }
}