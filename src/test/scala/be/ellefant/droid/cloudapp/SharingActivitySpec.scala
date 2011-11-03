package be.ellefant.droid.cloudapp

import android.accounts.Account
import android.content.Intent
import com.xtremelabs.robolectric.shadows.ShadowToast

class SharingActivitySpec extends CloudrSpecs {

  "SharingActivity" should {

    "show toast when onAccountFailure is called" in new context {
      accountManagerMock.getAccountsByType(AccountType) returns Nil
      activity.onCreate(null)
      ShadowToast.getTextOfLatestToast must be_== ("This action requires a CloudApp account")
    }

    "show toast when onAccountSuccess is called" in new context {
      val intent = new Intent
      intent.putExtra(Intent.EXTRA_TEXT, "http://google.com")
      activity.setIntent(intent)
      val acc = new Account("sdb", AccountType)
      accountManagerMock.getAccountsByType(AccountType) returns List(acc)
      accountManagerMock.getPassword(acc) returns "blabla"
      activity.onCreate(null)
      ShadowToast.getTextOfLatestToast must be_== ("URL will be saved to CloudApp")
    }
  }

  trait context extends RoboContext
      with Bindings.ThreadUtilBinding
      with Mocks.AccountManagerMock
      with Mocks.CloudAppMock {

    val activity = new SharingActivity
  }

}