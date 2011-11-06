package be.ellefant.droid.cloudapp

class ThreadUtil {
  def performOnBackgroundThread(r: Runnable) = ThreadUtils.performOnBackgroundThread(r)
}