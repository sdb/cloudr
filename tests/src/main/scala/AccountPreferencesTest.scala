package be.ellefant.cloudr
package tests

import android.provider.Settings
import android.test.InstrumentationTestCase
import junit.framework.Assert._
import android.app.Instrumentation.ActivityMonitor
import android.content.{IntentFilter, Intent}

class AccountPreferencesTest extends InstrumentationTestCase
    with CloudrTestBase{

  // TODO
  // - test that account sreen shows a link to the General application settings
  // - test that clicking the button opens the General application settings

  override def setUp() {
    super.setUp()
    setupAccount()
  }

//  def testOpenAccountPrefs = {
//    // val activity = launchActivityWithIntent(null, null, new Intent(Settings.ACTION_SYNC_SETTINGS))
//    val monitor = new ActivityMonitor(new IntentFilter(Settings.ACTION_SYNC_SETTINGS), null, false)
//    getInstrumentation addMonitor(monitor)
//    val intent = new Intent(Settings.ACTION_SYNC_SETTINGS)
//      .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//      .putExtra(Settings.EXTRA_AUTHORITIES, Array(Authority))
//    getInstrumentation.getContext startActivity(intent)
//    val activity = monitor.waitForActivityWithTimeout(5000)
//    assertNotNull(activity)
//  }
}
