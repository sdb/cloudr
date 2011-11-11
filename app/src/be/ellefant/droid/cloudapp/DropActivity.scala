package be.ellefant.droid.cloudapp

import roboguice.activity.RoboActivity
import android.widget.TextView
import android.database.Cursor
import DatabaseHelper._
import DropActivity._
import java.util.Date
import android.widget.CheckBox
import java.text.SimpleDateFormat

class DropActivity extends RoboActivity
    with Base.AccountRequired {

  protected def onAccountSuccess(name: String) = {
    val intent = getIntent
    val id = intent.getLongExtra(KeyId, -1) // TODO: check valid id
    setTitle("Cloudr - Drop %d" % id)
    setContentView(R.layout.drop)
    val cursor = managedQuery(CloudAppProvider.ContentUri,
        Array(ColId, ColName, ColViewCounter, ColUrl, ColPrivate, ColCreatedAt, ColUpdatedAt, ColSource),
        "%s = %d" format (ColId, id), null, null)
    // TODO: check if item is found
    val drop = if (cursor.moveToFirst()) Some(Drop(cursor)) else Option.empty[Drop]
    
    val nameText = findViewById(R.id.dropTitle).asInstanceOf[TextView]
    nameText.setText(drop map (_.name.toString) getOrElse(""))
    
    val viewCount = findViewById(R.id.dropViewCount).asInstanceOf[TextView]
    viewCount.setText(drop map (_.viewCounter.toString) getOrElse(""))
    
    val pub = findViewById(R.id.dropPublic).asInstanceOf[CheckBox]
    pub.setChecked(drop map (!_.priv) getOrElse false)
    pub.setClickable(false)
    
    val created = findViewById(R.id.dropCreated).asInstanceOf[TextView]
    created.setText(drop map (d => ShortDateFormat.format(d.createdAt)) getOrElse("")) // TODO locale
    
    val updated = findViewById(R.id.dropUpdated).asInstanceOf[TextView]
    updated.setText(drop map (d => ShortDateFormat.format(d.updatedAt)) getOrElse(""))
    
    val urlText = findViewById(R.id.dropUrl).asInstanceOf[TextView]
    urlText.setText(drop map (_.url) getOrElse(""))
    
    val sourceText = findViewById(R.id.dropSource).asInstanceOf[TextView]
    sourceText.setText(drop map (_.source) getOrElse(""))
  }
}
 
object DropActivity {
  
  val ShortDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a")
  
  case class Drop(
      id: Long,
      name: String,
      viewCounter: Int,
      url: String,
      priv: Boolean,
      source: String,
      createdAt: Date,
      updatedAt: Date)
      
  object Drop {
	def apply(cursor: Cursor): Drop = {
	  Drop(id = cursor.getLong(0),
	      name = cursor.getString(1),
	      viewCounter = cursor.getInt(2),
	      url = cursor.getString(3),
	      priv = cursor.getInt(4) == 1,
	      createdAt = DateFormat.parse(cursor.getString(5)),
	      updatedAt = DateFormat.parse(cursor.getString(6)),
	      source = cursor.getString(7))
	}
  }
}