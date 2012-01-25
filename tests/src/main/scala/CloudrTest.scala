package be.ellefant.cloudr
package tests

import android.content.Intent
import android.test.InstrumentationTestCase
import junit.framework.Assert._

class CloudrTest extends InstrumentationTestCase
    with CloudrTestBase {

  override def setUp() {
    super.setUp()
  }

  def testPackageIsCorrect {
    assertEquals("be.ellefant.cloudr", getInstrumentation.getTargetContext.getPackageName)
  }

  def testLaunch = {
    setupAccount()
    val intent = new Intent("android.intent.action.MAIN")
      .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
      .addCategory("android.intent.category.LAUNCHER")
    val activity = launchActivityWithIntent("be.ellefant.cloudr", classOf[MainActivity], intent)
    assertNotNull(activity)
  }
}
