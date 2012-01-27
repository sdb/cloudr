package be.ellefant.cloudr

import android.content.Intent
import android.accounts.Account
import android.content.Context
import android.text.ClipboardManager
import com.xtremelabs.robolectric.shadows.{ShadowPreferenceManager, ShadowToast}
import com.xtremelabs.robolectric.Robolectric

class SharingServiceSpec extends CloudrSpecs {

  "when an intent is received and no account is available SharingService" should {
    "do nothing" in new context {
      accountManagerMock.getAccountsByType(AccountType) returns Array.empty
      sendIntent()
      there was no(cloudAppMock).bookmark(anyString, anyString)
    }
  }

  "when a bookmark intent is received SharingService" should {
    "make a request to create a bookmark" in new bookmarkContext {
      bookmark()
      there was one(cloudAppMock).bookmark(title, url)
    }
    "and when the request succeeds" in {
      "do not display a toast" in new bookmarkContext {
        ShadowToast.shownToastCount must be_== (0)
      }
      "save a drop description" in new bookmarkContext {
        val item = bookmark()
        there was one (dropManagerMock).insert(item)
      }
    }
    "and when the request fails" in {
      "display a toast" in new failedBookmarkContext {
        bookmark()
        ShadowToast.getTextOfLatestToast must  be_== ("CloudApp upload failed.")
      }
      "not save a drop description" in new failedBookmarkContext {
        bookmark()
        there was no (dropManagerMock).insert(any)
      }
    }
  }

  "when an upload intent is received SharingService" should {
    "make a request to upload a file" in pending
    "and when the request succeeds" in {
      "display a toast" in pending
      "save a drop description" in pending
    }
    "and when the request fails" in {
      "display a toast" in pending
      "not save a drop description" in pending
    }
  }

  "when an item is uploaded the SharingService" should {
    "check the 'copy to clipboard' preference" in {
      "copy the url to the clipboard" in new bookmarkContext {
        val item = bookmark()
        val clipboard = service.getSystemService(Context.CLIPBOARD_SERVICE).asInstanceOf[ClipboardManager]
        clipboard.getText must be_==("http://cl.ly/361w0L1b2r320T2u023V")
      }
      "not copy the url to the clipboard" in new bookmarkContext {
        val clipboard = service.getSystemService(Context.CLIPBOARD_SERVICE).asInstanceOf[ClipboardManager]
        ShadowPreferenceManager.getDefaultSharedPreferences(service).edit().putBoolean("copy_url", false).commit()
        val item = bookmark()
        clipboard.getText must beNull
      }
    }
  }

  "when an authentication error occurs the SharingService" should {
    "display a toast" in new ErrorContext(Cloud.Error.Auth, "CloudApp authorization failed.") {
      test()
    }
  }

  "when the upload limit is reached the SharingService" should {
    "display a toast" in new ErrorContext(Cloud.Error.Limit, "CloudApp upload limit reached.") {
      test()
    }
  }

  "when some other error occurs the SharingService" should {
    "display a toast" in new ErrorContext(Cloud.Error.Other, "CloudApp upload failed.") {
      test()
    }
  }

  trait context extends RoboContext
      with Mocks.AccountManagerMock
      with Mocks.CloudAppMock
      with Mocks.DropManagerMock {

    lazy val service = new SharingService

    lazy val url = "http://google.com"
    lazy val title = "Test"

    def sendIntent(intent: Intent): Unit = {
      service.onCreate()
      service.onHandleIntent(intent)
    }

    def sendIntent(): Unit = {
      val intent = new Intent(Intent.ACTION_SEND)
      intent.setType("text/plain")
      intent.putExtra(Intent.EXTRA_TEXT, url)
      intent.putExtra(Intent.EXTRA_SUBJECT, title)
      sendIntent(intent)
    }
  }

  trait AccountContext { self: context =>
    val acc = new Account("sdb", AccountType)
    accountManagerMock.getAccountsByType(AccountType) returns Array(acc)
    accountManagerMock.blockingGetAuthToken(acc, AuthTokenType, true) returns "blabla"
  }

  class ErrorContext(error: Cloud.Error.Error, msg: String) extends context with AccountContext {
    def test() = {
      cloudAppMock.bookmark(title, url) returns Left(Cloud.Error.Other)
      sendIntent()
      ShadowToast.getTextOfLatestToast must  be_== ("CloudApp upload failed.")
    }
  }
  
  trait bookmarkContext extends context {
    def bookmark() = {
      val acc = new Account("sdb", AccountType)
      accountManagerMock.getAccountsByType(AccountType) returns Array(acc)
      accountManagerMock.blockingGetAuthToken(acc, AuthTokenType, true) returns "blabla"
      val item = mock[Drop]
      item.url returns "http://cl.ly/361w0L1b2r320T2u023V"
      cloudAppMock.bookmark(title, url) returns Right(item)
      sendIntent()
      item
    }
  }

  trait failedBookmarkContext extends context {
    def bookmark() = {
      val acc = new Account("sdb", AccountType)
      accountManagerMock.getAccountsByType(AccountType) returns Array(acc)
      accountManagerMock.blockingGetAuthToken(acc, AuthTokenType, true) returns "blabla"
      cloudAppMock.bookmark(title, url) returns Left(Cloud.Error.Other)
      sendIntent()
    }
  }

}