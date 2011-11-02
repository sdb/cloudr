package be.ellefant.droid

package object cloudapp extends be.ellefant.droid.dsl.Implicits with Imports with Constants {
  implicit def string2cloudrString(s: String) = new CloudrString(s)
}

class CloudrString(s: String) {
  def %(args: Any*) = s.format(args:_*)
}

trait Imports {
  type Logging = com.weiglewilczek.slf4s.Logging
}

trait Constants {
  lazy val Id = "be.ellefant.droid.cloudapp"
  lazy val AuthTokenType = Id
  lazy val AccountType = Id

  lazy val KeyItemType = "%s.%s" format (Id, "ITEM_TYPE")
}