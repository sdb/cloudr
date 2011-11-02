package be.ellefant.droid.cloudapp

object ItemTypes {
  // TODO http://developer.android.com/resources/tutorials/views/hello-listview.html
  // The better practice is to reference a string array defined by an external resource, such as with a <string-array>
  // resource in your project res/values/strings.xml file.
  lazy val data = Seq("All", "Popular", "Bookmarks", "Images", "Text", "Archives", "Audio", "Video", "Other", "Trash")
}