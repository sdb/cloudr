package be.ellefant.cloudr

import android.os.Bundle
import android.accounts.{ AuthenticatorException, OperationCanceledException, AccountManagerFuture, Account }
import roboguice.activity.RoboActivity
import java.io.IOException

class AccountRequiredSpec extends CloudrSpecs {

  "AccountRequired" should {

    "call onSuccess when an account is available" in new context {
      testAccountAvailable
    }
    "call onSuccess when a new account is added" in new context {
      testAccountAdded
    }
    "call onFailure when the authenticator fails" in new context {
      testException(new AuthenticatorException)
    }
    "call onFailure when the new account operation is cancelled" in new context {
      testException(new OperationCanceledException)
    }
    "call onFailure when an IO error occurs" in new context {
      testException(new IOException)
    }
  }

  trait context extends RoboContext
      with Mocks.AccountManagerMock {

    lazy val activity = spy(new AccountRequiredSpy)

    def testAccountAvailable = {
      val acc = mock[Account]
      accountManagerMock.getPassword(acc) returns "blabla"
      accountManagerMock.getAccountsByType(AccountType) returns Array(acc)
      activity.onCreate(null)
      there was one(activity).onAccountSuccess()
    }

    def testAccountAdded = {
      accountManagerMock.getAccountsByType(AccountType) returns Array.empty
      val amf = mock[AccountManagerFuture[Bundle]]
      val b = new Bundle
      b.putString(android.accounts.AccountManager.KEY_ACCOUNT_NAME, "sdb")
      amf.getResult returns b
      accountManagerMock.addAccount(AccountType, AuthTokenType, null, null, activity, null, null) returns amf
      activity.onCreate(null)
      there was one(activity).onAccountSuccess()
    }

    def testException(e: Throwable) = {
      accountManagerMock.getAccountsByType(AccountType) returns Array.empty
      val amf = mock[AccountManagerFuture[Bundle]]
      amf.getResult throws e
      accountManagerMock.addAccount(AccountType, AuthTokenType, null, null, activity, null, null) returns amf
      activity.onCreate(null)
      there was one(activity).onAccountFailure()
    }
  }

  class AccountRequiredSpy extends RoboActivity with Logging with AccountRequired {
    def onAccountSuccess() = {}
    def onAccountFailure() = {}
  }

}