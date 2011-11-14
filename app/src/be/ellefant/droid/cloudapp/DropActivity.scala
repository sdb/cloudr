package be.ellefant.droid.cloudapp

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.view.{ MenuItem, Menu }
import android.widget.{ TextView, CheckBox }
import roboguice.activity.RoboActivity
import java.text.SimpleDateFormat
import java.util.Date
import DatabaseHelper._
import DropActivity._
import CloudAppManager._
import android.view.View

class DropActivity extends RoboActivity
    with Base.AccountRequired {

  private var drop: Option[Drop] = None

  protected def onAccountSuccess(name: String) = {
    val intent = getIntent
    val id = intent.getLongExtra(KeyId, -1) // TODO: check valid id
    // setTitle("Cloudr - Drop %d" % id)
    setTitle("Cloudr")
    setContentView(R.layout.drop)
    val cursor = managedQuery(CloudAppProvider.ContentUri,
      Array(ColId, ColName, ColViewCounter, ColUrl, ColPrivate, ColCreatedAt, ColUpdatedAt, ColSource, ColItemType, ColContentUrl),
      "%s = %d" format (ColId, id), null, null)
    // TODO: check if item is found
    drop = if (cursor.moveToFirst()) Some(Drop(cursor)) else Option.empty[Drop]

    val nameText = findViewById(R.id.dropTitle).asInstanceOf[TextView]
    nameText.setText(drop map (_.name.toString) getOrElse (""))
    nameText setOnClickListener (onClickName _)

    val viewCount = findViewById(R.id.dropViewCount).asInstanceOf[TextView]
    viewCount.setText(drop map (_.viewCounter.toString) getOrElse (""))

    val pub = findViewById(R.id.dropPublic).asInstanceOf[CheckBox]
    pub.setChecked(drop map (!_.priv) getOrElse false)
    pub.setClickable(false)

    val created = findViewById(R.id.dropCreated).asInstanceOf[TextView]
    created.setText(drop map (d ⇒ ShortDateFormat.format(d.createdAt)) getOrElse ("")) // TODO locale

    val updated = findViewById(R.id.dropUpdated).asInstanceOf[TextView]
    updated.setText(drop map (d ⇒ ShortDateFormat.format(d.updatedAt)) getOrElse (""))

    val urlText = findViewById(R.id.dropUrl).asInstanceOf[TextView]
    urlText.setText(drop map (_.url) getOrElse (""))
    urlText setOnClickListener (onClickUrl _)

    val sourceText = findViewById(R.id.dropSource).asInstanceOf[TextView]
    sourceText.setText(drop map (_.source) getOrElse (""))
  }

  override def onCreateOptionsMenu(menu: Menu) = {
    // Seq(R.string.open, R.string.browse, R.string.delete) foreach (i => menu.add(Menu.NONE, ))
    val inflater = getMenuInflater()
    inflater.inflate(R.menu.drop_menu, menu)
    super.onCreateOptionsMenu(menu)
    true
  }
  
  private def onClickUrl(view: View) = openBrowser()
  private def onClickName(view: View) = openApplication()
  
  private def openBrowser() = {
    drop foreach { d ⇒
      val browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(d.url))
      startActivity(browserIntent)
    }
  }
  
  private def openApplication() = {
    drop foreach { d ⇒
      d.itemType match {
        case ItemType.Bookmark ⇒
          val browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(d.contentUrl))
          startActivity(browserIntent)
        case _ ⇒
        // TODO open drop
      }
    }
  }

  override def onOptionsItemSelected(item: MenuItem) = item.getItemId match {
    case R.id.open ⇒
      openApplication()
      true
    case R.id.browse ⇒
    	openBrowser()
      true
    case R.id.delete ⇒
      true
    case _ ⇒ super.onOptionsItemSelected(item)
  }
}

object DropActivity {

  val ShortDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a")

  case class Drop(
    id: Long,
    itemType: ItemType.ItemType,
    name: String,
    viewCounter: Int,
    url: String,
    contentUrl: String,
    priv: Boolean,
    source: String,
    createdAt: Date,
    updatedAt: Date)

  object Drop {
    def apply(cursor: Cursor): Drop = {
      Drop(id = cursor.getLong(0),
        itemType = ItemType.withName(cursor.getString(8).capitalize),
        name = cursor.getString(1),
        viewCounter = cursor.getInt(2),
        url = cursor.getString(3),
        contentUrl = cursor.getString(9),
        priv = cursor.getInt(4) == 1,
        createdAt = DateFormat.parse(cursor.getString(5)),
        updatedAt = DateFormat.parse(cursor.getString(6)),
        source = cursor.getString(7))
    }
  }
}