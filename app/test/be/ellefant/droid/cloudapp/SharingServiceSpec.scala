package be.ellefant.droid.cloudapp

import android.content.Intent
import android.accounts.Account

class SharingServiceSpec extends CloudrSpecs {

  "SharingService" should {

    "create a new bookmark when there is an account available" in new context {
      val acc = new Account("sdb", AccountType)
      accountManagerMock.getAccountsByType(AccountType) returns List(acc)
      accountManagerMock.getPassword(acc) returns "blabla"
      sendIntent()
      there was one(cloudAppMock).createBookmark(title, url)
    }

    "do nothing when there is no account available" in new context {
      accountManagerMock.getAccountsByType(AccountType) returns Nil
      sendIntent()
      there was no(cloudAppMock).createBookmark(title, url)
    }

    "do nothing when the title or url of the bookmark are blank" in {
      pending
    }
  }

  trait context extends RoboContext
      with Mocks.AccountManagerMock
      with Mocks.CloudAppMock {

    lazy val service = new SharingService

    lazy val url = "http://google.com"
    lazy val title = "Test"

    def sendIntent() = {
      val intent = new Intent
      intent.putExtra(Intent.EXTRA_TEXT, url)
      intent.putExtra(Intent.EXTRA_SUBJECT, title)

      service.onCreate()
      service.onHandleIntent(intent)
    }
  }

}