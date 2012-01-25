package be.ellefant.cloudr
package tests

import android.test.ActivityInstrumentationTestCase2
import junit.framework.Assert._

class AccountPreferencesActivityTest extends ActivityInstrumentationTestCase2(classOf[AccountPreferencesActivity])
    with CloudrTestBase {

  override def setUp() {
    super.setUp()
    setupAccount()
  }

  def testPreconditions() {
    val prefScreen = getActivity.getPreferenceScreen
    assertNotNull(prefScreen)
    assertEquals(1, prefScreen.getPreferenceCount)
    assertNotNull(prefScreen.findPreference("copy_url"))
  }

}
