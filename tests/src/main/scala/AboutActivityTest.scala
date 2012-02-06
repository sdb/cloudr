package be.ellefant.cloudr
package tests

import android.test.InstrumentationTestCase
import junit.framework.Assert._
import android.os.Bundle

class AboutActivityTest extends InstrumentationTestCase with CloudrTestBase {

  def testLaunch = {
    val activity = launchActivity("be.ellefant.cloudr", classOf[AboutActivity], new Bundle)
    assertNotNull(activity)
  }

}
