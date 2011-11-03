package be.ellefant.droid.cloudapp

import roboguice.activity.RoboActivity
import android.widget.TextView

class DropsActivity extends RoboActivity
    with Base.AccountRequired {

  protected def onAccountSuccess(name: String) = {
    val intent = getIntent
    val itemType = intent.getStringExtra(KeyItemType)
    setTitle("Cloudr - %s" % itemType)
    setContentView(new TextView(this) {
      setText("TODO")
    })
  }
}