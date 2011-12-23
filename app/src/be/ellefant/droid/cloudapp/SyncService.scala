package be.ellefant.droid.cloudapp

import collection.mutable.ListBuffer
import org.json.JSONException
import com.cloudapp.api.model.CloudAppItem
import com.google.inject.Inject
import DatabaseHelper._
import android.accounts.Account
import android.content.{ SyncResult, ContentProviderClient, AbstractThreadedSyncAdapter }
import android.database.Cursor
import android.os.Bundle
import roboguice.service.RoboService
import android.content.ContentProvider
import android.accounts.AuthenticatorException
import android.accounts.OperationCanceledException
import java.util.Date
import scalaandroid._

// TODO: see http://developer.getcloudapp.com/list-items
class SyncService extends RoboService
    with Base.CloudrService
    with Service
    with Injection.AccountManager
    with Injection.ApiFactory {

  protected[cloudapp] lazy val syncAdapter = new CloudAppSyncAdapter

  bind {
    case Intent("android.content.SyncAdapter") ⇒
      syncAdapter.getSyncAdapterBinder
  }

  protected[cloudapp] class CloudAppSyncAdapter extends AbstractThreadedSyncAdapter(SyncService.this, true) {

    def onPerformSync(account: Account, extras: Bundle, authority: String,
      provider: ContentProviderClient, syncResult: SyncResult) {
      try {
        logger.debug("onPerformSync for '%s'" % account.name)
        extras match {
          case b ⇒
            logger.debug("Processing sync with extras " + extras)
            val p = provider.getLocalContentProvider
            Option(accountManager.blockingGetAuthToken(account, AuthTokenType, true)) match {
              case Some(pwd) ⇒
                processRequest(p, account, pwd, syncResult)
              case _ ⇒
                syncResult.stats.numAuthExceptions += 1
                logger.warn("No password available for CloudApp")
              // TODO
            }
          //      case _ =>
          //        logger.info("Didn't process sync with extras " + extras)
        }
      } catch {
        case e: AuthenticatorException ⇒
          syncResult.stats.numParseExceptions += 1
          logger.warn("error getting auth token", e)
        case e: OperationCanceledException ⇒
          logger.debug("sync operation canceled", e)
      }
    }

    protected def processRequest(provider: ContentProvider, account: Account, pwd: String, syncResult: SyncResult) {
      // provider.delete(CloudAppProvider.ContentUri, null, null)
      var existingCursor: Cursor = null
      var existing: Seq[(Long, Date)] = null
      try {
        existingCursor = provider.query(CloudAppProvider.ContentUri, Array(ColId, ColUpdatedAt), null, Array.empty, null)
        existing = if (!existingCursor.moveToFirst()) Seq.empty else {
          val l = new ListBuffer[(Long, Date)]
          while (!existingCursor.isAfterLast()) {
            val id = existingCursor.getLong(0)
            val updated = DateFormat.parse(existingCursor.getString(1))
            existingCursor.moveToNext()
            l += (id -> updated)
          }
          l.toSeq
        }
      } catch {
        case e ⇒
          syncResult.databaseError = true
          logger.warn("error retrieving existing items", e)
          return
      } finally {
        if (existingCursor != null) existingCursor.close()
      }
      logger.debug("existing items: " + existing)

      val api = apiFactory.create(account.name, pwd)

      val items = retrieve(api, pwd, syncResult, true) ++ retrieve(api, pwd, syncResult, false)

      val ids = items map (_.id)

      val deleted = existing.filterNot(e ⇒ ids.exists(_ == e._1))
      val inserted = ids.filterNot(i ⇒ existing.exists(e => e._1 == i))
      val updated = items filter { item =>
        existing find (_._1 == item.id) map (e => item.updatedAt.after(e._2)) getOrElse (false)
      }
      val toInsert = items filter (i ⇒ inserted exists (_ == i.id)) map (_.toContentValues)

      if (syncResult.hasError()) return

      try {
        deleted foreach { d ⇒
          provider.delete(CloudAppProvider.ContentUri, "%s = %d" format (ColId, d._1), Array.empty)
        }
        provider.bulkInsert(CloudAppProvider.ContentUri, toInsert.toArray)
        updated foreach { u =>
          provider.update(CloudAppProvider.ContentUri, u.toContentValues, "%s = %d" format (ColId, u.id), Array.empty)
        }
      } catch {
        case e ⇒
          logger.warn("update failed", e)
          syncResult.databaseError = true
      }
    }

  }

  def retrieve(api: Cloud, pwd: String, syncResult: SyncResult, deleted: Boolean) = {
    val itemsPerPage = 20
    var page = 1
    var tried = 0
    var finished = false
    val it = Iterator.continually {
      logger.debug("getting page " + page)
      val items: List[Cloud.Drop] = if (finished || tried > 2) List.empty else {
        api items(page, itemsPerPage, deleted) match {
          case Left(Cloud.Error.Json) =>
            syncResult.stats.numParseExceptions += 1
            tried += 1
            Nil
          case Left(Cloud.Error.Auth) =>
            accountManager.invalidateAuthToken(AccountType, pwd)
            syncResult.stats.numAuthExceptions += 1
            tried = 3
            Nil
          case Left(_) =>
            syncResult.stats.numIoExceptions += 1
            tried += 1
            Nil
          case Right(items) => items
        }
      }
      page += 1
      finished = items.size < itemsPerPage
      items
    } takeWhile (_.size > 0)

    it.flatten.toSeq
  }

}