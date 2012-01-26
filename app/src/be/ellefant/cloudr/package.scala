import com.weiglewilczek.slf4s.Logger
import scalaandroid._

package be.ellefant {

  package object cloudr extends Imports with Constants
      with ViewImplicits
      with AdapterViewImplicits {

    implicit def string2cloudrString(s: String) = new CloudrString(s)
  }

  package cloudr {

  import android.webkit.MimeTypeMap

  class CloudrString(s: String) {
      def %(args: Any*) = s.format(args: _*)
      def isBlank = s == null || s.length == 0
      def toBlankOption = if (isBlank) None else Some(s)
    }

    /**
     * Creates a Logger with the simple class name
     */
    trait CloudrLogging {
      def trim(s: String) = s substring (0, math min (s.length, 23))
      lazy val logger = Logger(loggerName)
      lazy val loggerName = trim("Cloudr/" + this.getClass.getSimpleName)
    }

    trait Imports {
      type Logging = CloudrLogging
    }

    trait Constants {
      lazy val Id = "be.ellefant.cloudr"
      lazy val AuthTokenType = Id
      lazy val AccountType = Id
      lazy val Authority = "cloudr"
      lazy val Provider = "cloudr"

      lazy val KeyItemType = "%s.%s" format (Id, "ITEM_TYPE")
      lazy val KeyId = "%s.%s" format (Id, "ID")
    }

    object MimeType {
      lazy val R = """(.+)/(.+)""".r
      def unapply(mt: String): Option[(String, String)] = mt.trim() match {
        case R(x, y) => Some((x, y))
        case _ => None
      }
    }

    object Extension {
      private lazy val mimeTypes = MimeTypeMap.getSingleton
      private lazy val supported = Seq("text", "image", "video", "audio", "application")
      def isSupported(mimeType: String) = mimeType match {
        case MimeType(m, _) => supported.contains(m)
        case _ => false
      }
      def unapply(mimeType: String): Option[String] =
        if (isSupported(mimeType)) Some(mimeTypes.getExtensionFromMimeType(mimeType)) else None
    }
  }
}