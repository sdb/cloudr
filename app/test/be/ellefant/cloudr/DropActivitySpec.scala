package be.ellefant.cloudr

import android.accounts.Account
import java.util.Date
import android.widget.{CheckBox, TextView}
import sdroid.robolectric.TestMenuItem
import android.content.{ContextWrapper, Intent}
import com.xtremelabs.robolectric.Robolectric._
import android.net.Uri
import com.xtremelabs.robolectric.shadows.ShadowToast
import Cloud._, CloudAppManager._

class DropActivitySpec extends CloudrSpecification { def is = sequential ^
  "DropActivity should" ^
    "show the drop details" ! Context().displayDetails ^
    "open the drop in a browser" ! Context().openBrowser ^
    "delete the drop" ! DeleteContext().withSuccess ^
    "not delete the drop with auth error" ! DeleteContext().withAuthError ^
    "not delete the drop with error" ! DeleteContext().withError ^
    "restore the drop" ! RestoreContext().withSuccess ^
    "not restore the drop with auth error" ! RestoreContext().withAuthError ^
    "not restore the drop with error" ! RestoreContext().withError ^
    "listen to content changes via the observer" ! pending ^
    "show menus" ! pending
  end

  case class Context() extends RoboContext
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

    def testMenuAction(id: Int, expected: => Intent) = this {
      val intent = new Intent()
      intent.putExtra(KeyId, drop.id)
      shadowOf(activity) setIntent intent
      activity onCreate null
      val menuItem = TestMenuItem(id)
      activity.onOptionsItemSelected(menuItem)
      val r = shadowOf(activity.asInstanceOf[ContextWrapper]).getNextStartedActivity
      shadowOf(r).equals(expected) must beTrue
    }

    def openBrowser = testMenuAction(R.id.browse, new Intent(Intent.ACTION_VIEW, Uri.parse(drop.url)))
  }
  
  case class DeleteContext() extends ApiContext {
    val apiCall = cloudAppMock delete _
    val expectedToastMessage = "This item will be removed."
    val actionId = R.id.delete
  }

  case class RestoreContext() extends ApiContext {
    val apiCall = cloudAppMock recover _
    val expectedToastMessage = "This item will be restored."
    val actionId = R.id.restore
  }

  abstract class ApiContext extends RoboContext
      with Mocks.AccountManagerMock
      with Mocks.CloudAppMock
      with Mocks.DropManagerMock
      with Bindings.ThreadUtilBinding {

    val acc = new Account("sdb", AccountType)
    accountManagerMock.getAccountsByType(AccountType) returns Array(acc)
    accountManagerMock.getPassword(acc) returns "blabla"

    val drop = Drop(0, "", "", "", true, true, "", ItemType.Bookmark, 0, "", None, None, "", new Date, new Date, None)
    dropManagerMock.find(0) returns (Some(drop))

    val activity = new DropActivity

    val apiCall: String => Either[Error.Error, Drop]
    def expectedToastMessage: String
    def actionId: Int

    def withSuccess = this {
      val intent = new Intent()
      intent.putExtra(KeyId, drop.id)
      shadowOf(activity) setIntent intent
      activity onCreate null

      (accountManagerMock blockingGetAuthToken (acc, AuthTokenType, true)) returns ("blabla")
      (apiCall(drop.href)) returns (Right(drop))

      val menuItem = TestMenuItem(actionId)
      activity.onOptionsItemSelected(menuItem)

      ShadowToast.getTextOfLatestToast must be_==(expectedToastMessage) and {
        there was one(dropManagerMock).update(drop)
      }
    }

    def withAuthError = this {
      val intent = new Intent()
      intent.putExtra(KeyId, drop.id)
      shadowOf(activity) setIntent intent
      activity onCreate null

      (accountManagerMock blockingGetAuthToken (acc, AuthTokenType, true)) returns ("blabla")
      (apiCall(drop.href)) returns (Left(Error.Auth))

      val menuItem = TestMenuItem(actionId)
      activity.onOptionsItemSelected(menuItem)

      ShadowToast.getTextOfLatestToast must be_==(expectedToastMessage) and {
        there was no(dropManagerMock).update(drop)
      } and {
        there was one(accountManagerMock).clearPassword(acc)
      } and {
        there was one(accountManagerMock).invalidateAuthToken(AccountType, "blabla")
      }
    }

    def withError = this {
      val intent = new Intent()
      intent.putExtra(KeyId, drop.id)
      shadowOf(activity) setIntent intent
      activity onCreate null

      (accountManagerMock blockingGetAuthToken (acc, AuthTokenType, true)) returns ("blabla")
      (apiCall(drop.href)) returns (Left(Error.Other))

      val menuItem = TestMenuItem(actionId)
      activity.onOptionsItemSelected(menuItem)

      ShadowToast.getTextOfLatestToast must be_==(expectedToastMessage) and {
        there was no(dropManagerMock).update(drop)
      }
    }
  }

}
