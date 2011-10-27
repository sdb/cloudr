package be.ellefant.droid.cloudapp

import android.os.Bundle
import android.widget.TextView
import android.app.Activity
import MainActivity._

class MainActivity extends Activity {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(new TextView(this) {
      setText("Hello!")
    })
  }
}

object MainActivity extends Logging {
  protected lazy val tag = classOf[MainActivity].getName
}