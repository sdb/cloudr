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
        builder.query(db.getWritableDatabase(), projection, selection, selectionArgs, null, null, sortOrder)
      case _ ⇒ null
    }
  }

  def getType(uri: Uri): String = {
    Matcher.`match`(uri) match {
      case 1 ⇒ "cloudapp.Items"
      case _ ⇒ null
    }
  }

  def delete(uri: Uri, where: String, whereArgs: Array[String]): Int = {
    Matcher.`match`(uri) match {
      case 1 ⇒ db.getWritableDatabase.delete(DatabaseHelper.TblItems, where, whereArgs)
      case _ ⇒ 0
    }
  }

  def insert(uri: Uri, initialValues: ContentValues) = {
    Matcher.`match`(uri) match {
      case 1 if initialValues != null ⇒
        val newID = db.getWritableDatabase().insert(DatabaseHelper.TblItems, DatabaseHelper.ColId, initialValues)
        Uri.withAppendedPath(uri, newID.toString)
      case _ ⇒ null
    }
  }

  def update(uri: Uri, values: ContentValues, where: String, whereArgs: Array[String]): Int = 0
}

object CloudAppProvider {
  val ContentUri = Uri.parse("content://cloudapp")
  val Matcher = new UriMatcher(UriMatcher.NO_MATCH) {
    addURI("cloudapp", null, 1)
  }
}

