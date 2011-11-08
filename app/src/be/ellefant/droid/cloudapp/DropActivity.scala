package be.ellefant.droid.cloudapp

import roboguice.activity.RoboActivity
import android.widget.TextView
import DatabaseHelper._

class DropActivity extends RoboActivity
    with Base.AccountRequired {

  protected def onAccountSuccess(name: String) = {
    val intent = getIntent
    val id = intent.getLongExtra(KeyId, -1) // TODO: check valid id
    setTitle("Cloudr - Drop %d" % id)
    setContentView(R.layout.drop)
    val cursor = managedQuery(CloudAppProvider.ContentUri,
        Array(ColId, ColName, ColViewCounter),
        "%s = %d" format (ColId, id), null, null)
    // TODO: check if item is found
    val drop = if (cursor.moveToFirst()) Some(Drop(cursor.getLong(0), cursor.getString(1), cursor.getInt(2))) else Option.empty[Drop]
    val nameText = findViewById(R.id.dropTitle).asInstanceOf[TextView]
    nameText.setText(drop map (_.name.toString) getOrElse(""))
    val viewCount = findViewById(R.id.dropViewCount).asInstanceOf[TextView]
    viewCount.setText(drop map (_.viewCounter.toString) getOrElse(""))
  }
}