package be.ellefant.cloudr

import android.accounts.Account
import android.widget.ListView
import android.content.{ Intent, ContextWrapper }
import android.app.ListActivity
import com.xtremelabs.robolectric.Robolectric._

class MainActivitySpec extends CloudrSpecs {

  "MainActivity" should {

    "show list of item types" in new success {
      activity.getListAdapter.getCount must be_==(1)
    }

    "trigger DropActivity when an item is clicked" in new success {
      val lv = activity.findViewById(android.R.id.list).asInstanceOf[ListView]
      lv.performItemClick(lv, 0, 0)
      val expectedIntent = new Intent()
      expectedIntent.setClass(activity, classOf[DropsActivity])
      expectedIntent.putExtra(KeyItemType, "All")
      val actualIntent = shadowOf(activity.asInstanceOf[ContextWrapper]).getNextStartedActivity
      shadowOf(actualIntent).equals(expectedIntent) must beTrue // TODO use specs2 and hamcrest? with hamcrest matchers from robolectric (e.g. StartedMatcher)
    }

    "finish when no account is available" in new failure {
      shadowOf(activity.asInstanceOf[ListActivity]).isFinishing must beTrue // TODO: write matcher: activity must beFinishing -> RoboSpecs matchers
    }
  }

  trait failure extends Base {
    accountManagerMock.getAccountsByType(AccountType) returns Array.empty
    activity.onCreate(null)
  }

  trait success extends Base {
    val acc = new Account("sdb", "blabla")
    accountManagerMock.getAccountsByType(AccountType) returns Array(acc)
    accountManagerMock.getPassword(acc) returns "blabla"
    activity.onCreate(null)
  }

  trait Base extends RoboContext
      with Bindings.ThreadUtilBinding
      with Mocks.AccountManagerMock
      with Mocks.CloudAppManagerMock {

    lazy val activity = new MainActivity
    cloudAppManagerMock.itemTypes returns (Array("All"))
  }

}