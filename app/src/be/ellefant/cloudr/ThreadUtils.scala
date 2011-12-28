package be.ellefant.cloudr

object ThreadUtils {

  def performOnBackgroundThread(r: Runnable) = new Thread(r) { this.start() }

  implicit def function2runnable(f: () ⇒ Any): Runnable = new Runnable {
    def run() {
      try { f() } finally {}
    }
  }
}