package be.ellefant.droid

package object cloudapp extends Imports with Constants
    with sdroid.Implicits {

  implicit def string2cloudrString(s: String) = new CloudrString(s)
}

class CloudrString(s: String) {
  def %(args: Any*) = s.format(args: _*)
  def isBlank = s == null || s.length == 0
  def toBlankOption = if (isBlank) None else Some(s)
}

trait Imports {
  type Logging = com.weiglewilczek.slf4s.Logging
}

trait Constants {
  lazy val Id = "be.ellefant.droid.cloudapp"
  lazy val AuthTokenType = Id
  lazy val AccountType = Id

  lazy val KeyItemType = "%s.%s" format (Id, "ITEM_TYPE")
  lazy val KeyId = "%s.%s" format (Id, "ID")
}