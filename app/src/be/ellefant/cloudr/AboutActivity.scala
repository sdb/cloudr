package be.ellefant.cloudr

import roboguice.activity.RoboActivity
import scalaandroid._
import android.os.Bundle

class AboutActivity extends RoboActivity with Activity with Logging
	with Injection.Config {
  
  override def onCreate(savedInstanceState: Bundle) = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.about)
    setTitle("About Cloudr %s" format config.version)
  }

}
