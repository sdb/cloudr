package be.ellefant.cloudr

class ThreadUtil {
  def performOnBackgroundThread(r: Runnable) = ThreadUtils.performOnBackgroundThread(r)
}