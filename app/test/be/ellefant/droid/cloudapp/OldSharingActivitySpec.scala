package be.ellefant.droid.cloudapp

import android.accounts.Account
import android.content.Intent
import com.xtremelabs.robolectric.shadows.ShadowToast

class OldSharingActivitySpec extends CloudrSpecs {

  "SharingActivity" should {
    "when an account is available show a toast with a success message and trigger SharingService" in new context { successToast }
    "when no account is available show a toast with a failure message and not trigger SharingService" in new context { failureToast }
  }

  trait context extends RoboContext
      with Bindings.ThreadUtilBinding
      with Mocks.AccountManagerMock
      with Mocks.CloudAppMock {

    val activity = new SharingActivity

    def accountAvailable() {
      val acc = new Account("sdb", AccountType)
      accountManagerMock.getAccountsByType(AccountType) returns Array(acc)
      accountManagerMock.getPassword(acc) returns "blabla"
    }

    def noAccountAvailable() {
      accountManagerMock.getAccountsByType(AccountType) returns Array.empty
    }

    def failureToast = {
      noAccountAvailable()
      testToast("This action requires a CloudApp account")
    }

    def successToast = {
      accountAvailable()
      val intent = new Intent
      intent.putExtra(Intent.EXTRA_TEXT, "http://google.com")
      activity.setIntent(intent)
      testToast("URL will be saved to CloudApp")
    }

    def testToast(msg: String) = {
      activity.onCreate(null)
      ShadowToast.getTextOfLatestToast must be_==(msg)
    }
  }

}