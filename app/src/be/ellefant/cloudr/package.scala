import com.weiglewilczek.slf4s.Logger
import scalaandroid._

package be.ellefant {

  package object cloudr extends Imports with Constants
      with ViewImplicits
      with AdapterViewImplicits {

    implicit def string2cloudrString(s: String) = new CloudrString(s)
  }

  package cloudr {

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

    object FileType extends Enumeration {
      val Jpg = Value("jpg", "jpeg")
      val Gif = Value("gif", "gif")
      val Png = Value("png", "png")
      val Bmp = Value("bmp", "bmp")

      val Gp3 = Value("3gp", "3gpp")
      val Mp4 = Value("mp4", "mp4")
      val Avi = Value("avi", "x-msvideo")
      val Wmv = Value("wmv", "x-ms-wmv")

      class FileType(val extension: String, val mimeType: String) extends Val()
      protected final def Value(extension: String, mimeType: String): FileType = new FileType(extension, mimeType)

      object Extension {
        def unapply(mimeType: String): Option[String] = values.collect {
          case ft: FileType if ft.mimeType == mimeType ⇒ ft.extension
        }.headOption
      }
    }
  }
}