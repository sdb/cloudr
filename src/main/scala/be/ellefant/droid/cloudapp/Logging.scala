package be.ellefant.droid.cloudapp

import android.util.Log

trait Logging {
  protected val tag = this.getClass.getName

  def logv(s: String) = Log.v(tag, s)
  def logv(s: String, e: Throwable) = Log.v(tag, s, e)

  def logd(s: String) = Log.d(tag, s)
  def logd(s: String, e: Throwable) = Log.d(tag, s, e)

  def logw(s: String) = Log.w(tag, s)
  def logw(s: String, e: Throwable) = Log.w(tag, s, e)

  def logi(s: String) = Log.i(tag, s)
  def logi(s: String, e: Throwable) = Log.i(tag, s, e)

  def loge(s: String) = Log.e(tag, s)
  def loge(s: String, e: Throwable) = Log.e(tag, s, e)

  def logwtf(s: String) = Log.wtf(tag, s)
  def logwtf(s: String, e: Throwable) = Log.wtf(tag, s, e)
}