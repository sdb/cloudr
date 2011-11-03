package be.ellefant.droid.cloudapp

import android.net.Uri
import android.database.Cursor
import android.content.{ContentValues, ContentProvider}

class CloudAppProvider extends ContentProvider with Logging {

  def onCreate: Boolean = {
    logger.info("onCreate")
    return true
  }

  def query(uri: Uri, projection: Array[String], selection: String, selectionArgs: Array[String], sortOrder: String): Cursor = {
    logger.info("query")
    null
  }

  def getType(uri: Uri): String = {
    logger.info("getType")
    null
  }

  def delete(uri: Uri, where: String, whereArgs: Array[String]): Int = {
    logger.info("delete")
    0
  }

  def insert(uri: Uri, initialValues: ContentValues) = {
    logger.info("insert")
    null
  }

  def update(uri: Uri, values: ContentValues, where: String, whereArgs: Array[String]): Int = {
    logger.info("update")
    0
  }
}

