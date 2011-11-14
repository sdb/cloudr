package be.ellefant.droid.cloudapp

import roboguice.activity.RoboListActivity
import android.widget.{ AdapterView, SimpleCursorAdapter, ArrayAdapter, TextView }
import android.content.Intent
import android.view.View
import DatabaseHelper._
import CloudAppManager._
import android.view.Menu

class DropsActivity extends RoboListActivity
    with Base.AccountRequired
    with Base.Default {

  protected def onAccountSuccess(name: String) = {
    val intent = getIntent
    val itemType = intent.getStringExtra(KeyItemType)
    setTitle("Cloudr - %s" % itemType) // FIXME: should show label instead of id
    setContentView(R.layout.drops)
    val projection = Array(ColId, ColName, ColUrl)
    val order = "%s DESC" format ColId
    val cursor = ItemType.withName(itemType) match {
      case ItemType.All ⇒
        managedQuery(CloudAppProvider.ContentUri, projection, null, null, order)
      case ItemType.Popular ⇒
        managedQuery(CloudAppProvider.ContentUri, projection, null, null, "%s DESC, %s" format (ColViewCounter, order))
      case it ⇒
        managedQuery(CloudAppProvider.ContentUri, projection, "item_type = ?", Array(it.toString.toLowerCase), order)
    }
    val displayFields = Array(ColName, ColUrl)
    val displayViews = Array(android.R.id.text1, android.R.id.text2)
    val adapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_2, cursor, displayFields, displayViews)
    setListAdapter(adapter)
    val lv = getListView
    lv setTextFilterEnabled true
    lv setOnItemClickListener (onItemClick _)
  }

  private def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long) {
    val intent = new Intent
    intent.putExtra(KeyId, id)
    intent.setClass(this, classOf[DropActivity])
    startActivity(intent)
  }
}