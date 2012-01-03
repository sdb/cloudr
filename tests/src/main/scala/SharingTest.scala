package be.ellefant.cloudr
package tests

import junit.framework.Assert._
import android.accounts.Account
import android.os.Bundle
import android.test.{ActivityInstrumentationTestCase2, AndroidTestCase}

class SharingTest extends ActivityInstrumentationTestCase2(classOf[MainActivity]) {

  override def setUp() {
    super.setUp()
    val am = android.accounts.AccountManager.get(this.getInstrumentation.getContext)
    val account = new Account("stefan@ellefant.be", AccountType)
    am.addAccountExplicitly(account, "blabla", new Bundle())
  }

  def testShareBookmark {

  }

}