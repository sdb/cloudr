package be.ellefant.droid.cloudapp

import com.google.inject.AbstractModule
import com.cloudapp.api.CloudApp
import org.specs2.specification.Context
import android.accounts.Account
import com.xtremelabs.robolectric.Robolectric._
import android.widget.{TextView, ListView}
import com.xtremelabs.robolectric.shadows.ShadowActivity
import android.content.{Intent, ContextWrapper}
import android.app.ListActivity

class MainActivitySpec extends CloudrSpecs {

  "MainActivity" should {
    "show list of item types" in new success {
      activity.getListAdapter.getCount must be_== (10)
    }
    "trigger DropActivity when an item is clicked" in new success {
      val lv = activity.findViewById(android.R.id.list).asInstanceOf[ListView]
      lv.performItemClick(lv, 0, 0)
      val expectedIntent = new Intent()
      expectedIntent.setClass(activity, classOf[DropsActivity])
      expectedIntent.putExtra(KeyItemType, "All")
      val actualIntent = shadowOf(activity.asInstanceOf[ContextWrapper]).getNextStartedActivity
      // shadowOf(actualIntent) must be_=== (expectedIntent)
      shadowOf(actualIntent).equals(expectedIntent) must beTrue
    }
    "finish when no account is available" in new failure {
      shadowOf(activity.asInstanceOf[ListActivity]).isFinishing must beTrue
    }
  }

  trait failure extends Base {
    accountManagerMock.getAccountsByType(AccountType) returns Nil
    activity.onCreate(null)
  }

  trait success extends Base {
    accountManagerMock.getAccountsByType(AccountType) returns List(new Account("sdb", AccountType))
    activity.onCreate(null)
  }

  trait Base extends Context with Robo {
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