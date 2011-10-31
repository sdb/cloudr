package be.ellefant.droid.cloudapp

import android.widget.TextView
import roboguice.activity.RoboActivity
class MainActivity extends RoboActivity with Logging with AccountRequired {

  protected def onAccountSuccess(name: String) = {
    setContentView(new TextView(this) {
      setText("Hello, %s!" format name)
    })
  }

  protected def onAccountFailure() = {
    finish()
  }
}