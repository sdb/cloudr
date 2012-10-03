package scalaandroid

import android.app.{ Service => AService }
import android.content.{ Intent => AIntent }
import android.os.IBinder

import collection.mutable.ListBuffer

trait Service {
  self: AService =>

  type BindCallback = PartialFunction[AIntent, IBinder]
  lazy val bindCallbacks = new ListBuffer[BindCallback]
  
  def bind(f: BindCallback) = bindCallbacks += f
  def notBound: PartialFunction[AIntent, IBinder] = { case _ => null}
  def onBind(intent: AIntent): IBinder =
    ((bindCallbacks reduceLeft (_ orElse _)) orElse notBound)(intent)
}