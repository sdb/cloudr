package be.ellefant.droid.cloudapp

import android.accounts.Account
import android.os.Bundle
import android.content.SyncResult
import android.database.Cursor
import android.content._
import com.google.inject.Inject
import collection.JavaConversions._
import com.cloudapp.api.model.CloudAppItem
import DatabaseHelper._
import com.cloudapp.api.CloudAppException
import org.apache.http.client.ClientProtocolException
import org.json.JSONException
import scala.collection.mutable.ListBuffer

class CloudAppSyncAdapter @Inject protected (context: Context) extends AbstractThreadedSyncAdapter(context, true)
    with Logging
    with Injection.AccountManager
    with Injection.ApiFactory {

  def onPerformSync (account: Account, extras: Bundle, authority: String,
                     provider: ContentProviderClient, syncResult: SyncResult) {
    logger.info("onPerformSync for '%s'" % account.name)
    extras match {
      case b =>
        logger.info("Processing sync with extras " + extras)
        val p = provider.getLocalContentProvider.asInstanceOf[CloudAppProvider]
        Option(accountManager.getPassword(account)) match {
          case Some(pwd) =>
            processRequest(p, account, pwd, syncResult)
          case _ =>
            logger.info("No password available")
            // TODO
        }
//      case _ =>
//        logger.info("Didn't process sync with extras " + extras)
    }
  }

  protected def processRequest(provider: CloudAppProvider, account: Account, pwd: String, syncResult: SyncResult) {
    // provider.delete(CloudAppProvider.ContentUri, null, null)
    var existingCursor: Cursor = null
    var existing: Seq[Long] = null 
    try {
      existingCursor = provider.query(CloudAppProvider.ContentUri, Array(ColId), null, Array.empty, null)
      existing = if (!existingCursor.moveToFirst()) Seq.empty else {
        val l = new ListBuffer[Long]
        while (!existingCursor.isAfterLast()) {
          val id = existingCursor.getLong(0)
          existingCursor.moveToNext()
          logger.info("id: " + id)
          l += id
        }
        l.toSeq
      }
    } catch {
      case e =>
        syncResult.databaseError = true
        logger.warn("error retrieving existing items", e)
        return
    } finally {
      if (existingCursor != null) existingCursor.close()
    }
    logger.info("existing " + existing)
    
    val api = apiFactory.create(account.name, pwd)
    val itemsPerPage = 20
    var page = 1
    var tried = 0
    var finished = false
    val it = Iterator.continually {
      logger.info("getting page " + page)
      var items: List[CloudAppItem] = if (finished || tried > 2) List.empty else
        try {
          api.getItems(page, itemsPerPage, null, true, null).toList 
        } catch {
          case e: CloudAppException if e.getCode() == 500 && e.getCause.isInstanceOf[JSONException] =>
            logger.warn("error fetching data", e)
            syncResult.stats.numParseExceptions += 1
            tried += 1
            Nil
          case e: CloudAppException if e.getCode() == 500 =>
            logger.warn("error fetching data", e)
            syncResult.stats.numIoExceptions += 1
            tried += 1
            Nil
          case e: CloudAppException if e.getCode() == 401 =>
            logger.warn("unauthenticated", e)
            syncResult.stats.numAuthExceptions += 1
            tried = 3
            Nil
        }
      page += 1
      finished = items.size < itemsPerPage
      items
    } takeWhile (_.size > 0)
    
    val items = it.flatten.toSeq
    val ids = items map (_.getId)
    
    val deleted = existing.filterNot(e => ids.exists(_ == e))
    val inserted = ids.filterNot(i => existing.exists(_ == i))
    val toInsert = items filter (i => inserted exists (_ == i.getId)) map (_.toContentValues)
    
    if (syncResult.hasError()) return
    
    try {
      deleted foreach { d =>
        provider.delete(CloudAppProvider.ContentUri, "%s = %d" format(ColId, d), Array.empty)
      }
      provider.bulkInsert(CloudAppProvider.ContentUri, toInsert.toArray)
    } catch {
      case e =>
        syncResult.databaseError = true
    }
  }

}