package be.ellefant.cloudr

import android.content.Context

class DropManager(context: Context) {

  def getContentResolver = context.getContentResolver

  def insert(drop: Drop) = {
    val provider = getContentResolver.acquireContentProviderClient(CloudAppProvider.ContentUri).getLocalContentProvider.asInstanceOf[CloudAppProvider]
    val db = provider.database.getWritableDatabase
    db beginTransaction ()
    try {
      db insert (DatabaseHelper.TblItems, DatabaseHelper.ColId, drop.toContentValues) // TODO check first
      provider.context.getContentResolver notifyChange (CloudAppProvider.ContentUri, null)
      db setTransactionSuccessful ()
    } catch {
      case e ⇒ // TODO
    }
    db endTransaction ()
  }
}
