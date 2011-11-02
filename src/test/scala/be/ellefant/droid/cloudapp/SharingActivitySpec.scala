package be.ellefant.droid.cloudapp

import android.accounts.Account
import android.content.Intent
import com.google.inject.AbstractModule
import com.cloudapp.api.CloudApp
import org.specs2.specification.Context
import com.xtremelabs.robolectric.shadows.ShadowToast

class SharingActivitySpec extends CloudrSpecs {

  "SharingActivity" should {
    "show toast when onAccountFailure is called" in {
      val activity = new SharingActivity
      activity.onAccountFailure
      ShadowToast.getTextOfLatestToast must be_== ("This action requires a CloudApp account")
    }
    "show toast when onAccountSuccess is called" in new context {
      val activity = new SharingActivity
      val intent = new Intent
      intent.putExtra(Intent.EXTRA_TEXT, "http://google.com")
      activity.setIntent(intent)
      val acc = new Account("sdb", AccountType)
      accountManagerMock.getAccountsByType(AccountType) returns List(acc)
      accountManagerMock.getPassword(acc) returns "blabla"
      activity.onAccountSuccess("sdb")
      // val testApi = there was one(apiMock).createBookmark ("", "")
      ShadowToast.getTextOfLatestToast must be_== ("URL will be saved to CloudApp")
    }
  }

  trait context extends Context with Robo {
    lazy val accountManagerMock = mock[AccountManager]
    lazy val apiMock = mock[CloudApp]

    object module extends AbstractModule {
      def configure() {
        bind(classOf[AccountManager]).toInstance(accountManagerMock)
        bind(classOf[ApiFactory]).toInstance(new ApiFactory {
          override def create(name: String, password: String) = apiMock
        })
      }
    }
  }

}