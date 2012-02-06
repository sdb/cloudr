package be.ellefant.cloudr

import android.content.{ Intent, ContentResolver }
import android.os.Bundle
import scalaandroid._

object Base extends sdroid.Types {

  trait CloudrActivity extends AActivity with Logging

  trait AccountRequired extends CloudrActivity with be.ellefant.cloudr.AccountRequired {
    protected[cloudr] def onAccountFailure() = {
      finish()
    }
  }

  trait Default extends Activity {
    self: Activity with Logging with AccountRequired ⇒

    optionsMenu { menu ⇒
      val inflater = getMenuInflater()
      inflater.inflate(R.menu.main_menu, menu)
    }

    optionsItemSelected {
      case MenuItem(R.id.sync) ⇒
        ContentResolver.requestSync(account(), Authority, new Bundle)
      case MenuItem(R.id.settings) ⇒
        val intent = new Intent("android.intent.action.VIEW")
        intent.setClass(this, classOf[AccountPreferencesActivity])
        startActivity(intent)
      case MenuItem(R.id.about) ⇒
        val intent = new Intent()
        intent.setClass(this, classOf[AboutActivity])
        startActivity(intent)
    }
  }

  trait CloudrService extends AService with Logging
}