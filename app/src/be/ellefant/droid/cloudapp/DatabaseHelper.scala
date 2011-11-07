package be.ellefant.droid.cloudapp

import android.content.Context
import DatabaseHelper._
import android.database.sqlite.{SQLiteDatabase, SQLiteOpenHelper}
import com.cloudapp.api.model.CloudAppItem
import android.content.ContentValues
import java.text.SimpleDateFormat
import android.database.Cursor

class DatabaseHelper(context: Context) extends SQLiteOpenHelper(context, DbName, null, 2) {

  def onCreate(db: SQLiteDatabase) = {
    db.execSQL(SqlCreate)
    db.execSQL(SqlCreateIndex)
  }

  def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = {
    db.execSQL(SqlDropIndex)
    db.execSQL(SqlDrop)
  }
}

object DatabaseHelper {
  val DbName = "cloudapp"
  val TblItems = "items"
  val IndItemsHref = "ind_items_href"
    
  val ColId = "_id"
  val ColHref = "href"
  val ColName = "name"
  val ColPrivate = "private"
  val ColSubscribed = "subscribed"
  val ColUrl = "url"
  val ColContentUrl = "content_url"
  val ColItemType = "item_type"
  val ColViewCounter = "view_counter"
  val ColIcon = "icon"
  val ColRemoteUrl = "remote_url"
  val ColRedirectUrl = "redirect_url"
  val ColThumbnailUrl = "thumbnail_url"
  val ColSource = "source"
  val ColCreatedAt = "created_at"
  val ColUpdatedAt = "updated_at"
  val ColDeletedAt = "deleted_at"

  val SqlCreate = """CREATE TABLE %s (
    %s INTEGER PRIMARY KEY,
    %s TEXT NOT NULL, -- href
    %s TEXT NOT NULL, -- name
    %s INTEGER NOT NULL, -- private
    %s INTEGER NOT NULL, -- subscribed
    %s TEXT NOT NULL,
    %s TEXT NOT NULL,
    %s TEXT NOT NULL,
    %s INTEGER NOT NULL,
    %s TEXT NOT NULL,
    %s TEXT,
    %s TEXT,
    %s TEXT,
    %s TEXT NOT NULL,
    %s TEXT NOT NULL,
    %s TEXT NOT NULL,
    %s TEXT
)""" format (TblItems, ColId, ColHref, ColName, ColPrivate, ColSubscribed, ColUrl, ColContentUrl, ColItemType,
    ColViewCounter, ColIcon, ColRemoteUrl, ColRedirectUrl, ColThumbnailUrl, ColSource,
    ColCreatedAt, ColUpdatedAt, ColDeletedAt)

  val SqlCreateIndex = "CREATE INDEX %s ON %s (%s)" format (IndItemsHref, TblItems, ColHref)

  val SqlDrop = "DROP TABLE IF EXISTS " + TblItems
  
  val SqlDropIndex = "DROP INDEX IF EXISTS " + IndItemsHref
  
  val DateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS") // ISO8601
  
  class RichCloudAppItem(item: CloudAppItem) {
    def toContentValues = {
      val values = new ContentValues()
      values.put(ColId, item.getId)
      values.put(ColHref, item.getHref)
      values.put(ColName, item.getName)
      values.put(ColPrivate, item.isPrivate)
      values.put(ColSubscribed, item.isSubscribed)
      values.put(ColUrl, item.getUrl)
      values.put(ColContentUrl, item.getContentUrl)
      values.put(ColItemType, item.getItemType.toString.toLowerCase)
      values.put(ColViewCounter, new Integer(item.getViewCounter.toInt))
      values.put(ColIcon, item.getIconUrl)
      if (item.getRemoteUrl == null)
        values.putNull(ColRemoteUrl)
      else
        values.put(ColRemoteUrl, item.getRemoteUrl)
      if (item.getRedirectUrl == null)
        values.putNull(ColRedirectUrl)
      else
        values.put(ColRedirectUrl, item.getRedirectUrl)
      values.put(ColSource, item.getSource)
      values.put(ColCreatedAt, DateFormat.format(item.getCreatedAt))
      values.put(ColUpdatedAt, DateFormat.format(item.getUpdatedAt))
      if (item.getRedirectUrl == null)
        values.putNull(ColRedirectUrl)
      else
        values.put(ColRedirectUrl, item.getRedirectUrl)
      values
    }
  }
  
  implicit def richCloudAppItem(item: CloudAppItem) = new RichCloudAppItem(item)
}