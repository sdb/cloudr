package be.ellefant.droid.cloudapp

import android._
import app.Activity
import os.Bundle
import widget.TextView

class Main extends Activity {
  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(new TextView(this) {
      setText("Hello!")
    })
  }
}