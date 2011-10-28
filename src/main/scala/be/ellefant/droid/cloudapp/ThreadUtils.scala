package be.ellefant.droid.cloudapp

object ThreadUtils {

  def performOnBackgroundThread(r: Runnable) = new Thread(r) {
    start
  }

  implicit def function2runnable(f: () => Any): Runnable = new Runnable {
    def run() {
      try { f() } finally {}
    }
  }

  implicit def lazy2runnable(f: => Any): Runnable = function2runnable({() => f})
}