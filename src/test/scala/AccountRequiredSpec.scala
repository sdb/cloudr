package be.ellefant.droid.cloudapp
package tests

import be.ellefant.droid.cloudapp.{AccountRequired, Logging, MainActivity}
import org.specs2.mutable._
import org.specs2.mock.Mockito
import org.specs2.matcher.{Expectable, Matcher}
import org.specs2.specification.BeforeExample
import com.github.jbrechtel.robospecs.RoboSpecs
import roboguice.activity.RoboActivity
import roboguice.RoboGuice
import com.xtremelabs.robolectric.Robolectric
import com.google.inject.AbstractModule
import com.google.inject.util.Modules
import android.os.Bundle
import android.accounts.{OperationCanceledException, AccountManagerFuture, Account}

class AccountRequiredSpec extends RoboSpecs with Mockito {
  args(sequential=true)

  "onCreate" should {
    "call onSuccess when an account is available" in new context {
      accountManagerMock.getAccountsByType(AccountType) returns List(mock[Account])
      test(true)
    }
    "call onSuccess when a new account is added" in new context {
      accountManagerMock.getAccountsByType(AccountType) returns Nil
      val amf = mock[AccountManagerFuture[Bundle]]
      val b = new Bundle
      b.putString(android.accounts.AccountManager.KEY_ACCOUNT_NAME, "sdb")
      amf.getResult returns b
      accountManagerMock.addAccount(AccountType, AuthTokenType, activity) returns amf
      test(true)
    }
    "call onFailure when the new account operation is cancelled" in new context {
      accountManagerMock.getAccountsByType(AccountType) returns Nil
      val amf = mock[AccountManagerFuture[Bundle]]
      amf.getResult throws new OperationCanceledException
      accountManagerMock.addAccount(AccountType, AuthTokenType, activity) returns amf
      test(false)
    }
  }

  trait context extends After {
    val accountManagerMock = mock[AccountManager]
    val activity = new AccountRequiredSpy()

    RoboGuice.setBaseApplicationInjector(Robolectric.application, RoboGuice.DEFAULT_STAGE, Modules.`override`(RoboGuice.newDefaultRoboModule(Robolectric.application)).`with`(new TestModule()))

    def after = RoboGuice.util.reset()

    def test(success: Boolean) = {
      activity.onCreate(null)
      (activity.success must be_== (success)) && (activity.failure must be_== (!success))
    }

    class TestModule extends AbstractModule {
      def configure() {
        bind(classOf[AccountManager]).toInstance(accountManagerMock)
        bind(classOf[ThreadUtil]).toInstance(new ThreadUtil {
          def performOnBackgroundThread(r: Runnable) { r.run() }
        })
      }
    }
  }

  class AccountRequiredSpy extends RoboActivity with Logging with AccountRequired {
    var success = false
    var failure = false
    def onAccountSuccess(name: String) = success = true
    def onAccountFailure() = failure = true
  }

}