package be.ellefant.cloudr

import android.accounts.Account
import com.xtremelabs.robolectric.Robolectric._
import CloudAppManager._
import java.util.Date
import android.widget.{CheckBox, TextView}
import sdroid.robolectric.TestMenuItem
import android.content.{ContextWrapper, Intent}
import com.xtremelabs.robolectric.Robolectric._
import android.net.Uri

class DropActivitySpec extends CloudrSpecification { def is =
  "DropActivity should" ^
    "show the drop details" ! Context.displayDetails ^
    "open the drop in a browser" ! Context.openBrowser
  end

  case object Context extends RoboContext
      with Mocks.AccountManagerMock
      with Mocks.CloudAppMock
      with Mocks.DropManagerMock {

    val acc = new Account("sdb", AccountType)
    accountManagerMock.getAccountsByType(AccountType) returns Array(acc)
    accountManagerMock.getPassword(acc) returns "blabla"

    val drop = Drop(0, "", "", "", true, true, "", ItemType.Bookmark, 0, "", None, None, "", new Date, new Date, None)
    dropManagerMock.find(0) returns (Some(drop))

    val activity = new DropActivity
    
    def displayDetails = this {
      val intent = new Intent()
      intent.putExtra(KeyId, drop.id)
      shadowOf(activity) setIntent intent
      activity onCreate null
      activity.getTitle must be_==("Cloudr") and {
        val view = (activity findViewById (R.id.dropTitle)).asInstanceOf[TextView]
        view.getText must be_==(drop.name)
      } and {
        val view = (activity findViewById (R.id.dropViewCount)).asInstanceOf[TextView]
        view.getText.toString.toLong must be_==(drop.viewCounter)
      } and {
        val v = (activity.findViewById(R.id.dropPublic)).asInstanceOf[CheckBox]
        v.isChecked must be_!= (drop.priv)
      } and {
        val view = (activity findViewById (R.id.dropUrl)).asInstanceOf[TextView]
        view.getText must be_==(drop.url)
      } and {
        val view = (activity findViewById (R.id.dropSource)).asInstanceOf[TextView]
        view.getText must be_==(drop.source)
      }
    }

    def openBrowser = this {
      val intent = new Intent()
      intent.putExtra(KeyId, drop.id)
      shadowOf(activity) setIntent intent
      activity onCreate null
      val menuItem = TestMenuItem(R.id.browse)
      activity.onOptionsItemSelected(menuItem)
      val r = shadowOf(activity.asInstanceOf[ContextWrapper]).getNextStartedActivity
      shadowOf(r).equals(new Intent(Intent.ACTION_VIEW, Uri.parse(drop.url))) must beTrue
    }
  }

}
