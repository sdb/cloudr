package be.ellefant.droid.cloudapp

import android.content.Context
import DatabaseHelper._
import android.database.sqlite.{SQLiteDatabase, SQLiteOpenHelper}

class DatabaseHelper(context: Context) extends SQLiteOpenHelper(context, DbName, null, 1) {

  def onCreate(db: SQLiteDatabase) = {
    db.execSQL(SqlCreate)
  }

  def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) = {
    db.execSQL(SqlDrop)
  }
}

object DatabaseHelper {
  val DbName = "cloudapp"
  val TblItems = "items"
  val ColId = "_id"
  val ColName = "name"
  val ColUrl = "url"
  val ColDeletedAt = "deleted_at"
  val ColItemType = "item_type"

  val SqlCreate = """CREATE TABLE %s (
    %s INTEGER PRIMARY KEY AUTOINCREMENT,
    %s TEXT,
    %s TEXT,
    %s TEXT
)""" format (TblItems, ColId, ColName, ColUrl, ColItemType)

  val SqlDrop = "DROP TABLE IF EXISTS " + TblItems
}