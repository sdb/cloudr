package be.ellefant.droid.cloudapp

import org.specs2.specification.Context
import com.google.inject.AbstractModule
import com.cloudapp.api.CloudApp
import android.content.Intent
import android.accounts.Account

class SharingServiceSpec extends CloudrSpecs {

  "SharingService" should {

    "create a new bookmark when there is an account available" in new context {
      val acc = new Account("sdb", AccountType)
      accountManagerMock.getAccountsByType(AccountType) returns List(acc)
      accountManagerMock.getPassword(acc) returns "blabla"
      sendIntent()
      there was one(apiMock).createBookmark(title, url)
    }

    "do nothing when there is no account available" in new context {
      accountManagerMock.getAccountsByType(AccountType) returns Nil
      sendIntent()
      there was no(apiMock).createBookmark(title, url)
    }

    "do nothing when the title or url of the bookmark are blank" in {
      pending
    }
  }

  trait context extends Context with Robo {
    lazy val accountManagerMock = mock[AccountManager]
    lazy val apiMock = mock[CloudApp]
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