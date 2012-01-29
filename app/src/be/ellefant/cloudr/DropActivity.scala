package be.ellefant.cloudr

import android.net.Uri
import android.widget.{ TextView, CheckBox }
import android.view.View
import android.widget.Toast
import android.content.Intent
import roboguice.activity.RoboActivity
import java.text.SimpleDateFormat
import scalaandroid._
import DatabaseHelper._, DropActivity._, CloudAppManager._, ThreadUtils._, Cloud._

class DropActivity extends RoboActivity
    with Activity
    with DropsContentObservingActivity
    with Base.AccountRequired
    with Base.Default
    with Injection.ApiFactory
    with Injection.ThreadUtil
    with Injection.DropManager {

  private var drop: Option[Drop] = None

  optionsMenu { menu ⇒
    val inflater = getMenuInflater()
    inflater.inflate(R.menu.drop_menu, menu)
    val visible = drop map (!_.deleted) getOrElse false
    (2 to 3) foreach (i ⇒ menu getItem (i) setVisible (visible)) // 'Open' and 'Delete' menu items
    menu getItem (4) setVisible (!visible) // 'Restore' menu item
    true
  }

  optionsItemSelected {
    //    case MenuItem(R.id.open) ⇒
    //      openApplication()
    case MenuItem(R.id.browse) ⇒ openBrowser()
    case MenuItem(R.id.delete) ⇒ deleteDrop()
    case MenuItem(R.id.restore) => restoreDrop()
  }

  protected def onAccountSuccess() = {
    val intent = getIntent
    val id = intent.getLongExtra(KeyId, -1) // TODO: check valid id
    setTitle("Cloudr")
    setContentView(R.layout.drop)
    initView(id)
    initObserver
  }
  
  private def initView(id: Long) = {
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
  
  private def deleteDrop() = drop foreach { d => // TODO: send intent to service and process in a service instead of spawning a thread ?
    val acc = account()
    val pwd = accountManager blockingGetAuthToken (account, AuthTokenType, true)
    val toast = Toast.makeText(getApplicationContext, "This item will be removed.", Toast.LENGTH_SHORT)
    toast.show()
    threadUtil.performOnBackgroundThread { () ⇒
      val api = apiFactory.create(acc.name, pwd)
      api.delete(d.href) match {
        case Right(drop) ⇒
          dropManager.update(drop)
        case Left(Error.Auth) ⇒
          accountManager.clearPassword(acc)
          accountManager.invalidateAuthToken(AccountType, pwd)
        case Left(error) ⇒
        // TODO api error
      }
    }
    finish()
  }

  private def restoreDrop() = drop foreach { d =>
    val acc = account()
    val pwd = accountManager blockingGetAuthToken (account, AuthTokenType, true)
    val toast = Toast.makeText(getApplicationContext, "This item will be restored.", Toast.LENGTH_SHORT)
    toast.show()
    threadUtil.performOnBackgroundThread { () ⇒
      val api = apiFactory.create(acc.name, pwd)
      api.recover(d.href) match {
        case Right(drop) ⇒
          dropManager.update(drop)
        case Left(Error.Auth) ⇒
          accountManager.clearPassword(acc)
          accountManager.invalidateAuthToken(AccountType, pwd)
        case Left(error) ⇒
        // TODO api error
      }
    }
    finish()
  }

  protected def onDropsChanges = drop foreach (d => initView(d.id))
}

object DropActivity {
  val ShortDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a")
}