package be.ellefant.cloudr

import android.content.Context
import DatabaseHelper._
import android.database.Cursor

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

  def update(drop: Drop) = {
    val provider = getContentResolver.acquireContentProviderClient(CloudAppProvider.ContentUri).getLocalContentProvider.asInstanceOf[CloudAppProvider]
    val db = provider.database.getWritableDatabase
    db beginTransaction ()
    try {
      db update (DatabaseHelper.TblItems, drop.toContentValues, "%s = %d" format (ColId, drop.id), Array.empty)
      provider.context.getContentResolver.notifyChange(CloudAppProvider.ContentUri, null)
      db setTransactionSuccessful ()
    } catch {
      case e ⇒
      // TODO
    }
    db endTransaction ()
  }

  def find(id: Long) = {
    var cursor: Cursor = null
    try {
      cursor = getContentResolver.query(CloudAppProvider.ContentUri,
        Array(ColId, ColName, ColViewCounter, ColUrl, ColPrivate, ColCreatedAt, ColUpdatedAt, ColSource, ColItemType,
          ColContentUrl, ColHref, ColDeletedAt, ColSubscribed, ColIcon, ColRemoteUrl, ColRedirectUrl), "%s = %d" format (ColId, id), null, null)
      if (cursor.moveToFirst()) Some(Drop(cursor)) else Option.empty[Drop]
    } catch {
      case _ =>  None // TODO
    } finally {
      if (cursor != null) cursor.close
    }
  }
}
