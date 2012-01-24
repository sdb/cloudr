package be.ellefant.cloudr
package tests

import android.accounts.Account
import android.os.Bundle
import android.test.ActivityInstrumentationTestCase2
import junit.framework.Assert._

class AccountPreferencesActivityTest extends ActivityInstrumentationTestCase2(classOf[AccountPreferencesActivity]) {

  override def setUp() {
    super.setUp()
    val am = android.accounts.AccountManager.get(this.getInstrumentation.getContext)
    val account = new Account("stefan@ellefant.be", AccountType)
    am.addAccountExplicitly(account, "blabla", new Bundle())
  }

  def testPreconditions() {
    val activity = getActivity
    val prefScreen = activity.getPreferenceScreen
    assertNotNull(prefScreen)
    assertEquals(0, prefScreen.getPreferenceCount)
  }

}
