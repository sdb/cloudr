package be.ellefant.droid {

  import com.weiglewilczek.slf4s.Logger

  package object cloudapp extends Imports with Constants
      with sdroid.Implicits {

    implicit def string2cloudrString(s: String) = new CloudrString(s)
  }

  package cloudapp {

    class CloudrString(s: String) {
      def %(args: Any*) = s.format(args: _*)
      def isBlank = s == null || s.length == 0
      def toBlankOption = if (isBlank) None else Some(s)
    }

    /**
     * Creates a Logger with the simple class name
     */
    trait CloudrLogging {
      lazy val logger = Logger(loggerName)
      lazy val loggerName = Option("Cloudr/" + this.getClass.getSimpleName) map (n ⇒ n substring (0, math min (n.length, 23))) get
    }

    trait Imports {
      type Logging = CloudrLogging
    }

    trait Constants {
      lazy val Id = "be.ellefant.droid.cloudapp"
      lazy val AuthTokenType = Id
      lazy val AccountType = Id

      lazy val KeyItemType = "%s.%s" format (Id, "ITEM_TYPE")
      lazy val KeyId = "%s.%s" format (Id, "ID")
    }
  }
}