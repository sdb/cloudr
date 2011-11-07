package be.ellefant.droid.cloudapp

import android.accounts.Account
import android.os.Bundle
import com.google.inject.Inject
import collection.JavaConversions._
import android.content._

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
            processRequest(p, account, pwd)
          case _ =>
            logger.info("No password available")
            // TODO
        }
//      case _ =>
//        logger.info("Didn't process sync with extras " + extras)
    }
  }

  protected def processRequest(provider: CloudAppProvider, account: Account, pwd: String) = {
    provider.delete(CloudAppProvider.ContentUri, null, null)
    logger.info("processingRequest")
    val api = apiFactory.create(account.name, pwd)
    // val stats = api.getAccountStats
    // val count = stats.getItems
    val itemsPerPage = 20
    // val pageCount = if (count == 0) 0 else ((count - 1) / itemsPerPage)
    var page = 1
    val it = Iterator.continually {
      logger.info("getting page " + page)
      val items = api.getItems(page, itemsPerPage, null, true, null)
      page += 1
      items map { item =>
        logger.info("received item " + item.getName)
        val values = new ContentValues()
        values.put(DatabaseHelper.ColName, item.getName)
        values.put(DatabaseHelper.ColUrl, item.getUrl)
        values.put(DatabaseHelper.ColItemType, item.getItemType.toString)
        values
      }
    } takeWhile (_.size == itemsPerPage)
    provider.bulkInsert(CloudAppProvider.ContentUri, it.flatten.toArray)
  }

}