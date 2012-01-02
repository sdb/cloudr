package be.ellefant.cloudr

import android.content.Context
import android.database.sqlite.{ SQLiteDatabase, SQLiteOpenHelper }
import java.text.SimpleDateFormat
import DatabaseHelper._

class DatabaseHelper(context: Context) extends SQLiteOpenHelper(context, DbName, null, 1) {

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
  val DbName = "cloudr"
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
    %s TEXT NOT NULL,
    %s TEXT NOT NULL,
    %s INTEGER NOT NULL,
    %s INTEGER NOT NULL,
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
}