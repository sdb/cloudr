package be.ellefant.droid.cloudapp

import android.accounts.Account
import android.content.{ SyncResult, ContentProviderClient, ContentProvider, ContentValues }
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import roboguice.RoboGuice
import com.cloudapp.api.CloudAppException
import com.xtremelabs.robolectric.Robolectric
import DatabaseHelper._

class SyncServiceSpec extends CloudrSpecs {

  "SyncService" should {
    "perform an initial sync" in syncInitial
    "perform an incremental sync" in pending
    "indicate an error when no password is found" in noPassword
    "indicate an error when a database exception occurs" in pending
    "indicate an error when the password is invalid" in invalidPassword
    "indicate an error when a JSON exception occurs" in pending
    "retry in case of an IO exception" in pending
    "indicate an error when more than 3 IO exception occur" in pending
    "handle sync cancel" in pending
  }

  def noPassword = new context {
    syncAdapter.onPerformSync(account, new Bundle, "cloudapp", contentProviderClient, syncResult)
    hasError && noMods
  }

  def invalidPassword = new context {
    accountManagerMock.getPassword(account) returns "sdb"
    cloudAppMock.getItems(1, 20, null, false, null) throws new CloudAppException(401, "", null)
    syncAdapter.onPerformSync(account, new Bundle, "cloudapp", contentProviderClient, syncResult)
    hasError && noMods
  }

  def syncInitial = new success {
    val cursor = mock[Cursor]
    contentProvider.query(CloudAppProvider.ContentUri, Array(ColId), null, Array.empty, null) returns cursor
    cursor.moveToFirst returns false
    syncAdapter.onPerformSync(account, new Bundle, "cloudapp", contentProviderClient, syncResult)
    val inserted = there was one(contentProvider).bulkInsert(CloudAppProvider.ContentUri, Array())
    noError && inserted
  }

  trait success extends context {
    accountManagerMock.blockingGetAuthToken(account, AuthTokenType, true) returns "sdb"
  }

  trait context extends RoboContext
      with Mocks.AccountManagerMock
      with Mocks.CloudAppMock {

    lazy val syncAdapter = RoboGuice.getInjector(Robolectric.application.getApplicationContext).getInstance(classOf[SyncService]).syncAdapter
    lazy val contentProvider = mock[ContentProvider]
    lazy val contentProviderClient = mock[ContentProviderClient]
    lazy val account = new Account("sdb", AccountType)
    lazy val syncResult = new SyncResult

    contentProviderClient.getLocalContentProvider returns contentProvider

    def noInsert = there was no(contentProvider).bulkInsert(any[Uri], any[Array[ContentValues]])
    def noDelete = there was no(contentProvider).delete(any[Uri], anyString, any[Array[String]])
    def noMods = noInsert && noDelete
    def hasError = syncResult.hasError must beTrue
    def noError = syncResult.hasError must beFalse
  }
}