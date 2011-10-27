package be.ellefant.droid.cloudapp

object ThreadUtils {

  def performOnBackgroundThread(f: () => Unit) = {
    val t = new Thread {
      override def run {
        try {
          f()
        }
        finally {
        }
      }
    }
    t.start
    t
  }
}