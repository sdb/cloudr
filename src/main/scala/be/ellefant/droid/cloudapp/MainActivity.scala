package be.ellefant.droid.cloudapp

import android.os.Bundle
import android.widget.TextView
import android.app.Activity
import MainActivity._

class MainActivity extends Activity with Logging with AccountRequired {
  protected lazy val tag = Tag

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    withAccount(onSuccess _, onFailure _)
  }

  private def onSuccess(name: String) {
    setContentView(new TextView(this) {
      setText("Hello, %s!" format name)
    })
  }

  private def onFailure() {
    finish()
  }
}

object MainActivity {
  protected lazy val Tag = classOf[MainActivity].getName
}