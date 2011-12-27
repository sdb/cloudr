package be.ellefant.droid.cloudapp

import android.widget.{ AdapterView, ArrayAdapter }
import android.content.Intent
import android.view.View
import roboguice.activity.RoboListActivity
import scalaandroid._

class MainActivity extends RoboListActivity
		with Activity
    with Base.AccountRequired
    with Base.Default
    with Injection.CloudAppManager
    with Injection.AccountManager {

  protected def onAccountSuccess() = {
    setContentView(R.layout.main) // using custom view, mainly to be able to test with Robolectric
    val adapter = new ArrayAdapter[String](this, android.R.layout.simple_list_item_1, cloudAppManager.itemTypes)
    setListAdapter(adapter)
    val lv = getListView
    lv setTextFilterEnabled true
    lv setOnItemClickListener (onItemClick _)
  }

  private def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long) {
    val intent = new Intent
    intent.putExtra(KeyItemType, CloudAppManager.ItemType.apply(position).toString())
    intent.setClass(this, classOf[DropsActivity])
    startActivity(intent)
  }
}