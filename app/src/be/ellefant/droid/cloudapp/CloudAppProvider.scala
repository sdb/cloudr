package be.ellefant.droid.cloudapp

import android.net.Uri
import android.database.Cursor
import android.content.{ UriMatcher, ContentValues, ContentProvider }
import android.database.sqlite.SQLiteQueryBuilder
import CloudAppProvider._

class CloudAppProvider extends ContentProvider with Logging {

  protected var db: DatabaseHelper = _ // TODO inject and mock ?

  def onCreate: Boolean = {
    db = new DatabaseHelper(this.getContext)
    true
  }

  def query(uri: Uri, projection: Array[String], selection: String, selectionArgs: Array[String], sortOrder: String): Cursor = {
    val builder = new SQLiteQueryBuilder
    builder.setTables("ITEMS")
    Matcher.`match`(uri) match {
      case 1 ⇒
        val c = builder query (db.getWritableDatabase(), projection, selection, selectionArgs, null, null, sortOrder)
        c setNotificationUri (getContext.getContentResolver, uri)
        c
      case _ ⇒ null
    }
  }

  def getType(uri: Uri): String = {
    Matcher.`match`(uri) match {
      case 1 ⇒ "cloudapp.Items"
      case _ ⇒ null
    }
  }

  // TODO: uri can include the ID
  def delete(uri: Uri, where: String, whereArgs: Array[String]): Int = {
    Matcher.`match`(uri) match {
      case 1 ⇒
      	val i = db.getWritableDatabase.delete(DatabaseHelper.TblItems, where, whereArgs)
      	getContext.getContentResolver.notifyChange(uri, null)
      	i
      case _ ⇒ 0
    }
  }

  def insert(uri: Uri, initialValues: ContentValues) = {
    Matcher.`match`(uri) match {
      case 1 if initialValues != null ⇒
        val newID = db.getWritableDatabase().insert(DatabaseHelper.TblItems, DatabaseHelper.ColId, initialValues)
      	getContext.getContentResolver.notifyChange(uri, null)
        Uri.withAppendedPath(uri, newID.toString)
      case _ ⇒ null
    }
  }

  // TODO: uri can include the ID
  def update(uri: Uri, values: ContentValues, where: String, whereArgs: Array[String]): Int = {
    Matcher.`match`(uri) match {
      case 1 if values != null ⇒
        val i = db.getWritableDatabase().update(DatabaseHelper.TblItems, values, where, whereArgs)
      	getContext.getContentResolver.notifyChange(uri, null)
      	i
      case _ ⇒ 0
    }
  }
}

object CloudAppProvider {
  val ContentUri = Uri.parse("content://cloudapp")
  val Matcher = new UriMatcher(UriMatcher.NO_MATCH) {
    addURI("cloudapp", null, 1)
  }
}

