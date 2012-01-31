package be.ellefant.cloudr

import android.accounts.Account

class DropActivitySpec extends CloudrSpecs {

  "DropActivity" should {
    "show the drop details" in new Context {
      activity onCreate null
    }
  }

  trait Context extends RoboContext
      with Mocks.AccountManagerMock
      with Mocks.CloudAppMock
      with Mocks.DropManagerMock {

    val acc = new Account("sdb", AccountType)
    accountManagerMock.getAccountsByType(AccountType) returns Array(acc)
    accountManagerMock.blockingGetAuthToken(acc, AuthTokenType, true) returns "blabla"

    val activity = new DropActivity
  }

}
