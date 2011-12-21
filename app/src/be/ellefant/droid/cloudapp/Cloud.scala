package be.ellefant.droid.cloudapp

import android.content.ContentValues
import com.cloudapp.api.{ CloudApp, CloudAppException }
import com.cloudapp.api.model.CloudAppItem
import java.io.InputStream
import java.util.Date
import CloudAppManager.ItemType
import DatabaseHelper._

/**
 * Small (temporary) wrapper for the CloudApp Java API.
 */
class Cloud(api: CloudApp) extends CloudrLogging {
	import Cloud._
  
	// TODO: add method to retrieve all items
  def bookmark(title: String, url: String) = trye(api.createBookmark(title, url))
  def upload(name: String, is: InputStream, length: Long) = trye(api.upload(is, name, length))
  
  def trye(f: => CloudAppItem): Either[Error.Error, Drop] = try {
    Right(Drop(f))
  } catch {
    case e: CloudAppException if e.getCode == 402 =>
      logger info("CloudApp authorization error", e)
      Left(Error.Auth)
    case e: CloudAppException if e.getCode == 200 =>
      logger info("CloudApp API limit", e)
      Left(Error.Limit)
    case e =>
      logger info("CloudApp API error", e)
      Left(Error.Other)
  }  
}

object Cloud {
  object Error extends Enumeration {
    type Error = Value
    val Auth, Limit, Other = Value
  }
  
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
      if (remoteUrl.isDefined)
        values.putNull(ColRemoteUrl)
      else
        values.put(ColRemoteUrl, remoteUrl.get)
      if (redirectUrl.isDefined)
        values.putNull(ColRedirectUrl)
      else
        values.put(ColRedirectUrl, redirectUrl.get)
      values.put(ColSource, source)
      values.put(ColCreatedAt, DateFormat.format(createdAt))
      values.put(ColUpdatedAt, DateFormat.format(updatedAt))
      if (deletedAt.isDefined)
        values.putNull(ColDeletedAt)
      else
        values.put(ColDeletedAt, DateFormat.format(deletedAt.get))
      values
    }
  }
  
  object Drop {
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
  }
}