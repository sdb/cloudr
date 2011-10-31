package be.ellefant.droid.cloudapp

trait ThreadUtil {
  def performOnBackgroundThread(r: Runnable): Unit
}

class ThreadUtilImpl extends ThreadUtil {
  def performOnBackgroundThread(r: Runnable) = ThreadUtils.performOnBackgroundThread(r)
}