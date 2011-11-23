package be.ellefant.droid.cloudapp

import android.content.ContentValues
import com.cloudapp.api.{ CloudApp, CloudAppException }
import com.cloudapp.api.model.CloudAppItem
import java.io.InputStream
  
class Cloud(api: CloudApp) {
	import Cloud._
  
  def bookmark(title: String, url: String) = trye(api.createBookmark(title, url))
  def upload(name: String, is: InputStream, length: Long) = trye(api.upload(is, name, length))
  
  def trye(f: => CloudAppItem): Either[Error.Error, Drop] = try {
    Right(Drop(f))
  } catch {
    case e: CloudAppException if e.getCode == 402 => Left(Error.Auth)
    case e: CloudAppException if e.getCode == 200 => Left(Error.Limit)
    case e_ => Left(Error.Other)
  }  
}

object Cloud {
  import DatabaseHelper._
  
  object Error extends Enumeration {
    type Error = Value
    val Auth, Limit, Other = Value
  }
  
  case class Drop(id: Long, url: String) {
    def toContentValues = {
      val values = new ContentValues
      values.put(ColId, new java.lang.Long(id))
      values.put(ColUrl, url)
      // TODO map to content values
      values
    }
  }
  
  object Drop {
    def apply(item: CloudAppItem): Drop = Drop(0, "") // TODO map drop
  }
}