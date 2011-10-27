package be.ellefant.droid.cloudapp

import android.util.Log

trait Logging {
  protected def tag: String

  def logd(s: String) = Log.d(tag, s)
  def logw(s: String) = Log.w(tag, s)
  def logi(s: String) = Log.i(tag, s)
  def loge(s: String) = Log.e(tag, s)
}