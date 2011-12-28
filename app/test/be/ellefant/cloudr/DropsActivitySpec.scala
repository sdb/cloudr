package be.ellefant.cloudr

import android.accounts.Account
import android.content.Intent
import android.content.ContentValues
import CloudAppManager._
import DatabaseHelper._
import com.xtremelabs.robolectric.Robolectric._

class DropsActivitySpec extends CloudrSpecs {

  "DropsActivity" should {

    "show all drops when invoked with an item type" in new context {
      pending
      /*val cv = new ContentValues()
      cv.put(ColId, new Integer(1))
      cv.put(ColName, "name")
      cv.put(ColItemType, ItemType.Bookmark.toString.toLowerCase)
      val shadow = shadowOf(activity)
      val resolver = shadow.getContentResolver
      resolver.insert(CloudAppProvider.ContentUri, cv)
      accountManagerMock.getAccountsByType(AccountType) returns Array(new Account("sdb", AccountType))
      val intent = new Intent
      intent.putExtra(KeyItemType, ItemType.All.toString)
      activity.setIntent(intent)
      activity.onCreate(null)
      shadow.getListAdapter.getCount must be_== (1)*/
    }

    "show all drops when invoked with an unknown item type" in {
      pending
    }
  }

  trait context extends RoboContext
      with Bindings.ThreadUtilBinding
      with Mocks.AccountManagerMock {
    lazy val activity = new DropsActivity
  }
}