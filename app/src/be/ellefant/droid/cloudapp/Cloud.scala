package be.ellefant.droid.cloudapp

import android.content.ContentValues
import com.cloudapp.api.{ CloudApp, CloudAppException }
import com.cloudapp.api.model.CloudAppItem
import java.io.InputStream
import android.util.Log
import be.ellefant.droid.cloudapp.CloudAppManager.ItemType
import java.util.Date
  
class Cloud(api: CloudApp) {
	import Cloud._
  
  def bookmark(title: String, url: String) = trye(api.createBookmark(title, url))
  def upload(name: String, is: InputStream, length: Long) = trye(api.upload(is, name, length))
  
  def trye(f: => CloudAppItem): Either[Error.Error, Drop] = try {
    Right(Drop(f))
  } catch {
    case e: CloudAppException if e.getCode == 402 => Left(Error.Auth)
    case e: CloudAppException if e.getCode == 200 => Left(Error.Limit)
    case e =>
      Log.w("CLOUDR", "failure", e)
      Left(Error.Other)
  }  
}

object Cloud {
  import DatabaseHelper._
  
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
      updatedAt: Date) {
    
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
      remoteUrl foreach { u =>
        values.put(ColRemoteUrl, u)
      }
      redirectUrl foreach { u =>
        values.put(ColRedirectUrl, u)
      }
      values.put(ColSource, source)
      values.put(ColCreatedAt, DateFormat.format(createdAt))
      values.put(ColUpdatedAt, DateFormat.format(updatedAt))
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
      updatedAt = item.getUpdatedAt
    )
  }
}