package be.ellefant.cloudr
package tests

import android.content.Intent
import android.test.InstrumentationTestCase
import junit.framework.Assert._
import android.widget.TextView

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

    val view = (activity findViewById(be.ellefant.cloudr.R.id.dropTitle)).asInstanceOf[TextView]
    logger warn ("text: " + view.getText)
  }
}
