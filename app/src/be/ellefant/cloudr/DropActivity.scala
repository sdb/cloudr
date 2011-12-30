package be.ellefant.cloudr

import android.net.Uri
import android.widget.{ TextView, CheckBox }
import roboguice.activity.RoboActivity
import java.text.SimpleDateFormat
import android.view.View
import android.widget.Toast
import scalaandroid._
import android.content.Intent
import DatabaseHelper._, DropActivity._, CloudAppManager._, ThreadUtils._

class DropActivity extends RoboActivity
    with Activity
    with Base.AccountRequired
    with Base.Default
    with Injection.ApiFactory
    with Injection.ThreadUtil {

  private var drop: Option[Drop] = None

  optionsMenu { menu =>
    val inflater = getMenuInflater()
    inflater.inflate(R.menu.drop_menu, menu)
    val visible = drop map(!_.deleted) getOrElse false
    (2 to 3) foreach (i => menu getItem(i) setVisible(visible))
    true
  }

  optionsItemSelected {
//    case MenuItem(R.id.open) ⇒
//      openApplication()
    case MenuItem(R.id.browse) ⇒
    	openBrowser()
    case MenuItem(R.id.delete) ⇒
    	drop foreach { d => // TODO: send intent to service and process in a service instead of spawning a thread ?
		  	val acc = account()
		  	val pwd = accountManager blockingGetAuthToken(account, AuthTokenType, true)
		  	val toast = Toast.makeText(getApplicationContext, "This item will be removed.", Toast.LENGTH_SHORT)
		  	toast.show()
		  	threadUtil.performOnBackgroundThread { () =>
          val api = apiFactory.create(acc.name, pwd)
          api.delete(d.href) match {
            case Right(drop) =>
              val provider = getContentResolver.acquireContentProviderClient(CloudAppProvider.ContentUri).getLocalContentProvider.asInstanceOf[CloudAppProvider]
              val db = provider.database.getWritableDatabase
              db beginTransaction()
              try {
                db update(DatabaseHelper.TblItems, drop.toContentValues, "%s = %d" format (ColId, drop.id), Array.empty)
                provider.context.getContentResolver.notifyChange(CloudAppProvider.ContentUri, null)
                db setTransactionSuccessful()
              } catch {
                case e => // TODO
              }
              db endTransaction()
            case Left(error) =>
              accountManager.clearPassword(acc)
              accountManager.invalidateAuthToken(AccountType, pwd)
              // TODO api error
          }
		  	}
		  	finish()
    	}
  }

  protected def onAccountSuccess() = {
    val intent = getIntent
    val id = intent.getLongExtra(KeyId, -1) // TODO: check valid id
    // setTitle("Cloudr - Drop %d" % id)
    setTitle("Cloudr")
    setContentView(R.layout.drop)
    val cursor = managedQuery(CloudAppProvider.ContentUri,
      Array(ColId, ColName, ColViewCounter, ColUrl, ColPrivate, ColCreatedAt, ColUpdatedAt, ColSource, ColItemType,
        ColContentUrl, ColHref, ColDeletedAt, ColSubscribed, ColIcon, ColRemoteUrl, ColRedirectUrl), "%s = %d" format (ColId, id), null, null)
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
}