package be.ellefant.droid.cloudapp

import android.os.Bundle
import android.widget.TextView
import android.app.Activity

class MainActivity extends Activity with Logging with AccountRequired {
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