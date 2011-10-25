package be.ellefant.droid.cloudapp

import android.util.Log

trait Logging {
  def tag: String

  def logd(s: => String) = Log.d(tag, s)
  def logw(s: => String) = Log.w(tag, s)
}