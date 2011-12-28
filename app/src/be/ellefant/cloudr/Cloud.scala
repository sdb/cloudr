package be.ellefant.cloudr

import java.io.InputStream
import collection.JavaConversions._
import com.cloudapp.api.{ CloudApp, CloudAppException }
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
}