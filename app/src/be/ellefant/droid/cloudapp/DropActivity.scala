package be.ellefant.droid.cloudapp

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.widget.{ TextView, CheckBox }
import roboguice.activity.RoboActivity
import java.text.SimpleDateFormat
import java.util.Date
import DatabaseHelper._
import DropActivity._
import CloudAppManager._
import android.view.View
import com.cloudapp.impl.model.CloudAppItemImpl
import scala.util.parsing.json.JSON
import org.json.JSONObject
import android.widget.Toast
import scalaandroid._
import ThreadUtils._

class DropActivity extends RoboActivity
    with Activity
    with Base.AccountRequired
    with Base.Default
    with Injection.ApiFactory
    with Injection.ThreadUtil {

  private var drop: Option[Drop] = None

  optionsMenu { menu =>
    // Seq(R.string.open, R.string.browse, R.string.delete) foreach (i => menu.add(Menu.NONE, ))
    val inflater = getMenuInflater()
    inflater.inflate(R.menu.drop_menu, menu)
    true
  }
  
  optionsItemSelected {
    case MenuItem(R.id.open) ⇒
      openApplication()
    case MenuItem(R.id.browse) ⇒
    	openBrowser()
    case MenuItem(R.id.delete) ⇒
    	drop foreach { d => // TODO: send intent to service and process in the background ?
		  	val acc = account()
		  	val pwd = accountManager.getPassword(acc)
		  	val toast = Toast.makeText(getApplicationContext, "This item will be removed.", Toast.LENGTH_SHORT)
		  	toast.show()
		  	threadUtil.performOnBackgroundThread { () =>
			  	val api = apiFactory.create(acc.name, pwd)
			  	val json = new JSONObject()
			  	json.put("href", d.href)
			  	val item = new CloudAppItemImpl(json)
			  	api.delete(item)
		  		getContentResolver().delete(CloudAppProvider.ContentUri, "%s = %d" format (ColId, d.id), Array.empty) 
		  	}
		  	finish()
    	}
  }

  protected def onAccountSuccess(name: String) = {
    val intent = getIntent
    val id = intent.getLongExtra(KeyId, -1) // TODO: check valid id
    // setTitle("Cloudr - Drop %d" % id)
    setTitle("Cloudr")
    setContentView(R.layout.drop)
    val cursor = managedQuery(CloudAppProvider.ContentUri,
      Array(ColId, ColName, ColViewCounter, ColUrl, ColPrivate, ColCreatedAt, ColUpdatedAt, ColSource, ColItemType, ColContentUrl, ColHref),
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
    href: String,
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
        href = cursor.getString(10),
        priv = cursor.getInt(4) == 1,
        createdAt = DateFormat.parse(cursor.getString(5)),
        updatedAt = DateFormat.parse(cursor.getString(6)),
        source = cursor.getString(7))
    }
  }
}