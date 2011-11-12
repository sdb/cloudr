package be.ellefant.droid.cloudapp

import android.content.Intent
import android.content.AbstractThreadedSyncAdapter
import android.accounts.Account
import android.os.Bundle
import android.content.SyncResult
import com.xtremelabs.robolectric.Robolectric
import be.ellefant.droid.cloudapp.SyncService.CloudAppSyncAdapter
import Robolectric._
import android.content.ContentProviderClient
import android.content.ContentProvider
import roboguice.RoboGuice
import DatabaseHelper._
import com.xtremelabs.robolectric.tester.android.database.TestCursor
import android.database.Cursor
import android.content.ContentValues

class SyncServiceSpec extends CloudrSpecs {

  "SyncService" should {
    "perform a sync" in syncNotItems
    "indicate an error when no password is found" in noPassword
    "indicate an error when a database exception occurs" in pending
    "handle sync cancel" in pending
  }

  def noPassword = new context {
    syncAdapter.onPerformSync(account, new Bundle, "cloudapp", contentProviderClient, syncResult)
    syncResult.hasError must beTrue
  }

  def syncNotItems = new success {
    val cursor = mock[Cursor]
    contentProvider.query(CloudAppProvider.ContentUri, Array(ColId), null, Array.empty, null) returns cursor
    cursor.moveToFirst returns false
    syncAdapter.onPerformSync(account, new Bundle, "cloudapp", contentProviderClient, syncResult)
    val noError = syncResult.hasError must beFalse
    val inserted = there was one(contentProvider).bulkInsert(CloudAppProvider.ContentUri, Array())
    noError && inserted
  }

  trait success extends context {
    accountManagerMock.getPassword(account) returns "sdb"
  }

  trait context extends RoboContext
      with Mocks.AccountManagerMock
      with Mocks.CloudAppMock {

    lazy val syncAdapter = RoboGuice.getInjector(Robolectric.application.getApplicationContext).getInstance(classOf[SyncService.CloudAppSyncAdapter])
    lazy val contentProvider = mock[ContentProvider]
    lazy val contentProviderClient = mock[ContentProviderClient]
    lazy val account = new Account("sdb", AccountType)
    lazy val syncResult = new SyncResult

    contentProviderClient.getLocalContentProvider returns contentProvider
  }
}