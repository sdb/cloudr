package be.ellefant.cloudr
package tests

import android.content.Intent
import android.test.InstrumentationTestCase
import junit.framework.Assert._

class DropActivityTest extends InstrumentationTestCase
    with CloudrTestBase {

  override def setUp() {
    super.setUp()
  }

  def testLaunch = {
    setupAccount()
    val intent = new Intent
    intent.putExtra(KeyId, 0)
    val activity = launchActivityWithIntent("be.ellefant.cloudr", classOf[MainActivity], intent)
    assertNotNull(activity)
  }
}
