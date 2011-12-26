package be.ellefant.droid.cloudapp

import android.net.Uri
import android.database.Cursor
import android.database.sqlite.SQLiteQueryBuilder
import android.content.{ UriMatcher, ContentValues, ContentProvider }

class CloudAppProvider extends ContentProvider with Logging {
	import CloudAppProvider._

  protected var db: DatabaseHelper = _ // TODO inject and mock ?

  def database = db
  def context = getContext

  def onCreate: Boolean = {
    db = new DatabaseHelper(this.getContext)
    true
  }

  def query(uri: Uri, projection: Array[String], selection: String, selectionArgs: Array[String], sortOrder: String): Cursor = {
    Matcher.`match`(uri) match {
      case 1 ⇒
        val builder = new SQLiteQueryBuilder
        builder.setTables("ITEMS")
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

  def delete(uri: Uri, where: String, whereArgs: Array[String]): Int = 0
  def insert(uri: Uri, initialValues: ContentValues) = null
  def update(uri: Uri, values: ContentValues, where: String, whereArgs: Array[String]): Int = 0
}

object CloudAppProvider {
  val ContentUri = Uri.parse("content://cloudapp")  // TODO: create and use constant
  val Matcher = new UriMatcher(UriMatcher.NO_MATCH) {
    addURI("cloudapp", null, 1)
  }
}

