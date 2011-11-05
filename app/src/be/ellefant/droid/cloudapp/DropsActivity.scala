package be.ellefant.droid.cloudapp

import roboguice.activity.RoboListActivity
import android.widget.{SimpleCursorAdapter, ArrayAdapter, TextView}
import DatabaseHelper._

class DropsActivity extends RoboListActivity
    with Base.AccountRequired {

  protected def onAccountSuccess(name: String) = {
    val intent = getIntent
    val itemType = intent.getStringExtra(KeyItemType)
    setTitle("Cloudr - %s" % itemType)
    setContentView(R.layout.drops)
    val projection = Array(ColId, ColName, ColUrl)
    val cursor = itemType match {
      case "All" => managedQuery(CloudAppProvider.ContentUri, projection, null, null, null)
      case "Trash" => managedQuery(CloudAppProvider.ContentUri, projection, null, null, null) // TODO
      case "Popular" => managedQuery(CloudAppProvider.ContentUri, projection, null, null, null) // TODO
      case "Bookmarks" => managedQuery(CloudAppProvider.ContentUri, projection, "item_type = 'BOOKMARK'", null, null)
      case "Images" => managedQuery(CloudAppProvider.ContentUri, projection, "item_type = 'IMAGE'", null, null)
      case "Archives" => managedQuery(CloudAppProvider.ContentUri, projection, "item_type = 'ARCHIVE'", null, null)
      case _ => managedQuery(CloudAppProvider.ContentUri, projection, "item_type = ?", Array(itemType.toUpperCase), null)
    }
    val displayFields = Array(ColName, ColUrl)
    val displayViews = Array(android.R.id.text1, android.R.id.text2)
    val adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, displayFields, displayViews)
    setListAdapter(adapter)
    val lv = getListView
    lv setTextFilterEnabled true
  }
}