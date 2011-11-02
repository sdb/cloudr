package be.ellefant.droid.cloudapp

import android.content.Intent
import com.google.inject.AbstractModule
import com.cloudapp.api.CloudApp
import org.specs2.specification.Context
import android.widget.ListView
import android.accounts.Account
import com.xtremelabs.robolectric.Robolectric

class MainActivitySpec extends CloudrSpecs {

  "MainActivity" should {
    "show list of item types" in new context {
      val acc = new Account("sdb", AccountType)
      accountManagerMock.getAccountsByType(AccountType) returns List(acc)
      activity.onCreate(null)
      activity.getListAdapter.getCount must be_== (10)
    }
    "finish when no account is available" in {
      pending
    }
  }

  trait context extends Context with Robo {
    lazy val accountManagerMock = mock[AccountManager]
    lazy val apiMock = mock[CloudApp]
    lazy val activity = new MainActivity

    object module extends AbstractModule {
      def configure() {
        bind(classOf[AccountManager]).toInstance(accountManagerMock)
        bind(classOf[ThreadUtil]).toInstance(new ThreadUtil {
          def performOnBackgroundThread(r: Runnable) { r.run() }
        })
      }
    }
  }

}