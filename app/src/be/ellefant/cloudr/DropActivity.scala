package be.ellefant.cloudr

import android.net.Uri
import android.widget.{ TextView, CheckBox }
import android.widget.Toast
import android.content.Intent
import roboguice.activity.RoboActivity
import java.text.SimpleDateFormat
import scalaandroid._
import CloudAppManager._, Cloud._
import android.accounts.Account
import DropActivity._
import android.view.{Menu, View}

class DropActivity extends RoboActivity
    with Activity
    with DropsContentObservingActivity
    with Base.AccountRequired
    with Base.Default
    with Injection.ApiFactory
    with Injection.DropManager {

  private var drop: Option[Drop] = None

  override def onCreateOptionsMenu(menu: Menu) = {
    optionsMenuCallbacks.reverse foreach (_(menu))
    optionsMenuCallbacks.nonEmpty
  }

  optionsMenu { menu ⇒
    val inflater = getMenuInflater()
    inflater.inflate(R.menu.drop_menu, menu)
    val visible = drop map (!_.deleted) getOrElse false
    (0 to 1) foreach (i ⇒ menu getItem (i) setVisible (visible)) // 'Open' and 'Delete' menu items
    menu getItem (2) setVisible (!visible) // 'Restore' menu item
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
    // TODO: check if item is found
    drop = dropManager.find(id)

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
    ApiTask("This item will be removed."){ api =>
      api.delete(d.href)
    }
  }

  private def restoreDrop() = drop foreach { d =>
    ApiTask("This item will be restored."){ api =>
      api.recover(d.href)
    }
  }

  protected def onDropsChanges = drop foreach (d => initView(d.id))

  object ApiTask {
    def apply(toastMessage: String)(f: Cloud => Either[Error.Error, Drop]) = {
      val task = new ApiTask(toastMessage, f)
      task.execute()
      finish()
    }
  }

  class ApiTask(toastMessage: String, f: Cloud => Either[Error.Error, Drop]) extends ScalaAsyncTask[Void,  Void,  Void] {
    var acc: Account = _
    var pwd: String = _

    override def onPreExecute() = {
      acc = account()
      pwd = accountManager blockingGetAuthToken (account, AuthTokenType, true)
      val toast = Toast.makeText(getApplicationContext, toastMessage, Toast.LENGTH_SHORT)
      toast.show()
    }

    def doInBackground(): Void = {
      val api = apiFactory.create(acc.name, pwd)
      f(api) match {
        case Right(drop) ⇒
          dropManager.update(drop)
        case Left(Error.Auth) ⇒
          accountManager.clearPassword(acc)
          accountManager.invalidateAuthToken(AccountType, pwd)
        case Left(error) ⇒
        // TODO api error
      }
      null
    }

    override def onPostExecute(void: Void) = {}
  }
}

object DropActivity {
  val ShortDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm a")
}