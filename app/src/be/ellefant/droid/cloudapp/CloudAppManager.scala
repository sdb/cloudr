package be.ellefant.droid.cloudapp

class CloudAppManager extends Injection.Resources {
  def itemTypes: Array[String] = resources.getStringArray(R.array.item_types)
}

object CloudAppManager {
  object ItemType extends Enumeration {
    type ItemType = Value
    val All, Popular, Bookmark, Image, Text, Archive, Audio, Video, Unknown, Trash = Value
  }
}