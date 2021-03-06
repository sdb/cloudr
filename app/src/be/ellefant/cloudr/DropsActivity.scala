package be.ellefant.cloudr

import android.widget.{ AdapterView, SimpleCursorAdapter, BaseAdapter }
import android.content.Intent
import android.view.View
import android.database.ContentObserver
import android.os.Handler
import roboguice.activity.RoboListActivity
import scalaandroid._
import DatabaseHelper._, CloudAppManager._

class DropsActivity extends RoboListActivity
    with Activity
    with DropsContentObservingActivity
    with Base.AccountRequired
    with Base.Default
    with Injection.CloudAppManager {

  private var adapter: BaseAdapter = _

  protected def onAccountSuccess() = {
    val intent = getIntent
    val itemType = intent.getStringExtra(KeyItemType)
    setTitle(cloudAppManager.itemTypes(ItemType.withName(itemType).id))
    setContentView(R.layout.drops)
    val projection = Array(ColId, ColName, ColViewCounter)
    val order = "%s DESC" format ColId
    val cursor = ItemType.withName(itemType) match {
      case ItemType.All ⇒
        managedQuery(CloudAppProvider.ContentUri, projection, "deleted_at IS NULL", null, order)
      case ItemType.Popular ⇒
        managedQuery(CloudAppProvider.ContentUri, projection, "deleted_at IS NULL", null, "%s DESC, %s" format (ColViewCounter, order))
      case ItemType.Trash ⇒
        managedQuery(CloudAppProvider.ContentUri, projection, "deleted_at IS NOT NULL", null, order)
      case it ⇒
        managedQuery(CloudAppProvider.ContentUri, projection, "item_type = ? AND deleted_at IS NULL", Array(it.toString.toLowerCase), order)
    }
    val displayFields = Array(ColName, ColViewCounter)
    val displayViews = Array(R.id.title_entry, R.id.count_entry)
    adapter = new SimpleCursorAdapter(this, R.layout.drops_entry, cursor, displayFields, displayViews)
    setListAdapter(adapter)
    val lv = getListView
    lv setTextFilterEnabled true
    lv setOnItemClickListener (onItemClick _)

    initObserver
  }

  private def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long) {
    val intent = new Intent
    intent.putExtra(KeyId, id)
    intent.setClass(this, classOf[DropActivity])
    startActivity(intent)
  }

  protected def onDropsChanges = adapter.notifyDataSetChanged
}