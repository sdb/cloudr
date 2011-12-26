package be.ellefant.droid.cloudapp

import java.util.Date
import java.text.ParseException
import android.content.ContentValues
import android.database.Cursor
import com.cloudapp.api.model.CloudAppItem
import CloudAppManager.ItemType
import DatabaseHelper._

case class Drop(
   id: Long,
   url: String,
   href: String,
   name: String,
   priv: Boolean,
   subscribed: Boolean,
   contentUrl: String,
   itemType: ItemType.ItemType,
   viewCounter: Long,
   iconUrl: String,
   remoteUrl: Option[String],
   redirectUrl: Option[String],
   source: String,
   createdAt: Date,
   updatedAt: Date,
   deletedAt: Option[Date]) {

  val deleted = deletedAt.isDefined

  def toContentValues = {
    val values = new ContentValues()
    values.put(ColId, new java.lang.Long(id))
    values.put(ColHref, href)
    values.put(ColName, name)
    values.put(ColPrivate, priv)
    values.put(ColSubscribed, subscribed)
    values.put(ColUrl, url)
    values.put(ColContentUrl, contentUrl)
    values.put(ColItemType, itemType.toString.toLowerCase)
    values.put(ColViewCounter, new java.lang.Long(viewCounter))
    values.put(ColIcon, iconUrl)
    if (remoteUrl.isEmpty)
      values.putNull(ColRemoteUrl)
    else
      values.put(ColRemoteUrl, remoteUrl.get)
    if (redirectUrl.isEmpty)
      values.putNull(ColRedirectUrl)
    else
      values.put(ColRedirectUrl, redirectUrl.get)
    values.put(ColSource, source)
    values.put(ColCreatedAt, DateFormat.format(createdAt))
    values.put(ColUpdatedAt, DateFormat.format(updatedAt))
    if (deletedAt.isEmpty)
      values.putNull(ColDeletedAt)
    else
      values.put(ColDeletedAt, DateFormat.format(deletedAt.get))
    values
  }
}

object Drop {

  def apply(cursor: Cursor): Drop = Drop(
    id = cursor.getLong(0),
    itemType = ItemType.withName(cursor.getString(8).capitalize),
    name = cursor.getString(1),
    viewCounter = cursor.getInt(2),
    url = cursor.getString(3),
    contentUrl = cursor.getString(9),
    href = cursor.getString(10),
    priv = cursor.getInt(4) == 1,
    createdAt = DateFormat.parse(cursor.getString(5)),
    updatedAt = DateFormat.parse(cursor.getString(6)),
    deletedAt = Date(cursor.getString(11)),
    source = cursor.getString(7),
    subscribed = cursor.getInt(12) == 1,
    iconUrl = cursor.getString(13),
    remoteUrl = if (cursor.isNull(14)) None else Some(cursor.getString(14)),
    redirectUrl = if (cursor.isNull(15)) None else Some(cursor.getString(15))
  )

  def apply(item: CloudAppItem): Drop = Drop(
    id = item.getId,
    url = item.getUrl,
    href = item.getHref,
    name = item.getName,
    priv = item.isPrivate,
    subscribed = item.isSubscribed,
    contentUrl = item.getContentUrl,
    itemType = ItemType.withName(item.getItemType.toString.toLowerCase.capitalize),
    viewCounter = item.getViewCounter,
    iconUrl = item.getIconUrl,
    remoteUrl = Option(item.getRemoteUrl),
    redirectUrl = Option(item.getRedirectUrl),
    source = item.getSource,
    createdAt = item.getCreatedAt,
    updatedAt = item.getUpdatedAt,
    deletedAt = Option(item.getDeletedAt)
  )

  object Date {
    def apply(s: String) = Option(s) flatMap { s =>
      try {
        Some(DateFormat.parse(s))
      } catch {
        case e: ParseException => None
      }
    }
  }
}