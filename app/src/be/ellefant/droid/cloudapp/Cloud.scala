package be.ellefant.droid.cloudapp

import collection.JavaConversions._
import android.content.ContentValues
import com.cloudapp.api.{ CloudApp, CloudAppException }
import com.cloudapp.api.model.CloudAppItem
import java.io.InputStream
import java.util.Date
import CloudAppManager.ItemType
import DatabaseHelper._
import com.cloudapp.impl.model.CloudAppItemImpl
import org.json.{JSONException, JSONObject}

/**
 * Small (temporary) wrapper for the CloudApp Java API.
 *
 * <p>All requests to CloudApp should go via this wrapper.</p>
 */
class Cloud(api: CloudApp) extends CloudrLogging {
	import Cloud._

  def bookmark(title: String, url: String) = trye(Drop(api.createBookmark(title, url)))
  def upload(name: String, is: InputStream, length: Long) = trye(Drop(api.upload(is, name, length)))
  def accountDetails() = trye(api.getAccountDetails)
  def delete(href: String) = trye {
    val json = new JSONObject()
    json.put("href", href)
    val item = new CloudAppItemImpl(json)
    Drop(api.delete(item))
  }
  def items(page: Int, itemsPerPage: Int, deleted: Boolean) = trye {
    api.getItems(page, itemsPerPage, null, deleted, null).toList map (Drop(_))
  }
  
  def trye[T](f: => T): Either[Error.Error, T] = try {
    Right(f)
  } catch {
    case e: CloudAppException if e.getCode == 402 =>
      logger info("CloudApp authorization error", e)
      Left(Error.Auth)
    case e: CloudAppException if e.getCode == 200 =>
      logger info("CloudApp API limit", e)
      Left(Error.Limit)
    case e: CloudAppException if e.getCode == 500 && e.getCause.isInstanceOf[JSONException] =>
      logger info("CloudApp JSON error", e)
      Left(Error.Json)
    case e =>
      logger info("CloudApp API error", e)
      Left(Error.Other)
  }  
}

object Cloud {

  /** Types of errors we are interested in ATM */
  object Error extends Enumeration {
    type Error = Value
    val Auth, Limit, Json, Other = Value
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