package be.ellefant.droid.cloudapp

import roboguice.activity.RoboPreferenceActivity
import android.os.Bundle

class AccountPreferencesActivity extends RoboPreferenceActivity
    with Base.Activity {
  
  override def onCreate(savedInstance: Bundle) {
    super.onCreate(savedInstance)
    addPreferencesFromResource(R.xml.account_preferences_activity)
  }

}