package be.ellefant.droid.cloudapp

import android.content.Intent
import android.accounts.Account
import com.cloudapp.api.model.CloudAppItem
import android.content.Context
import android.text.ClipboardManager

class OldSharingServiceSpec extends CloudrSpecs {

  "SharingService" should {

    "create a new bookmark and copy the URL to clipboard when there is an account available" in new context {
      val acc = new Account("sdb", AccountType)
      accountManagerMock.getAccountsByType(AccountType) returns Array(acc)
      accountManagerMock.getPassword(acc) returns "blabla"
      val item = mock[Cloud.Drop]
      item.url returns "http://cl.ly/361w0L1b2r320T2u023V"
      cloudAppMock.bookmark(title, url) returns Right(item)
      sendIntent()
      val created = there was one(cloudAppMock).bookmark(title, url)
      val clipboard = service.getSystemService(Context.CLIPBOARD_SERVICE).asInstanceOf[ClipboardManager]
      val clipped = clipboard.getText must be_==("http://cl.ly/361w0L1b2r320T2u023V")
      created && clipped
    }

    "create a new bookmark when there is an account available" in pending

    "do nothing when there is no account available" in new context {
      accountManagerMock.getAccountsByType(AccountType) returns Array.empty
      sendIntent()
      there was no(cloudAppMock).bookmark(title, url)
    }

    "do nothing when the title or url of the bookmark are blank" in {
      pending
    }

    "show a toast message when authentication fails" in pending

    "retry when an IO exception occurs" in pending

    "show a toast message when more than 3 IO exceptions occur" in pending
  }

  trait context extends RoboContext
      with Mocks.AccountManagerMock
      with Mocks.CloudAppMock {

    lazy val service = new SharingService

    lazy val url = "http://google.com"
    lazy val title = "Test"

    def sendIntent() = {
      val intent = new Intent
      intent.setType("text/plain")
      intent.putExtra(Intent.EXTRA_TEXT, url)
      intent.putExtra(Intent.EXTRA_SUBJECT, title)

      service.onCreate()
      service.onHandleIntent(intent)
    }
  }

}