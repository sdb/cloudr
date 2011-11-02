package be.ellefant.droid

package object cloudapp {

  implicit def string2cloudrString(s: String) = new CloudrString(s)

  lazy val Id = "be.ellefant.droid.cloudapp"
  lazy val AuthTokenType = Id
  lazy val AccountType = Id

  lazy val KeyItemType = "%s.%s" % (Id, "ITEM_TYPE")
}

class CloudrString(s: String) {
  def %(args: Any*) = s.format(args:_*)
}