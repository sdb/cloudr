package be.ellefant.cloudr
package tests

import android.test.ActivityInstrumentationTestCase2
import android.accounts.Account
import android.os.Bundle
import android.widget.ListView
import junit.framework.Assert._

class MainActivityTest extends ActivityInstrumentationTestCase2(classOf[MainActivity]) {

  override def setUp() {
    super.setUp()
    val am = android.accounts.AccountManager.get(this.getInstrumentation.getContext)
    val account = new Account("stefan@ellefant.be", AccountType)
    am.addAccountExplicitly(account, "blabla", new Bundle())
  }

  def testPreconditions() {
    val activity = getActivity
    val view = activity.findViewById(android.R.id.list).asInstanceOf[ListView]
    assertNotNull(view)
  }

}