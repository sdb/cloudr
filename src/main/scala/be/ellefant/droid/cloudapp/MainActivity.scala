package be.ellefant.droid.cloudapp

import android.widget.TextView
import android.app.Activity
import roboguice.activity.RoboActivity
import roboguice.inject.ContextScoped

@ContextScoped
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