package be.ellefant.droid.cloudapp

import android.widget.AdapterView.OnItemClickListener
import android.widget.{AdapterView, ArrayAdapter}
import android.content.Intent
import android.view.View
import com.google.inject.Inject
import roboguice.activity.RoboListActivity

class MainActivity extends RoboListActivity with Logging with AccountRequired {

  @Inject protected var cloudAppManager: CloudAppManager  = _

  protected[cloudapp] def onAccountSuccess(name: String) = {
    setContentView(R.layout.main) // using custom view, mainly to be able to test with Robolectric
    val adapter = new ArrayAdapter[String](this, android.R.layout.simple_list_item_1, cloudAppManager.itemTypes)
    setListAdapter(adapter)
    val lv = getListView
    lv.setTextFilterEnabled(true)
    lv.setOnItemClickListener(new OnItemClickListener() { // TODO: scala-ify, use function and provide implicits
      def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long) {
        val itemType = getListAdapter.getItem(position).asInstanceOf[String]
        val intent = new Intent
        intent.putExtra(KeyItemType, itemType)
        intent.setClass(MainActivity.this, classOf[DropsActivity])
        startActivity(intent)
      }
    })
  }

  protected[cloudapp] def onAccountFailure() = {
    finish()
  }
}