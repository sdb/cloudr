package be.ellefant.cloudr
package tests

import android.test.InstrumentationTestCase
import android.accounts.Account
import android.os.Bundle

trait CloudrTestBase {
  self: InstrumentationTestCase =>

  def setupAccount() = {
    val am = android.accounts.AccountManager.get(this.getInstrumentation.getContext)
    val account = new Account("stefan@ellefant.be", AccountType)
    am.addAccountExplicitly(account, "blabla", new Bundle())
  }
}
