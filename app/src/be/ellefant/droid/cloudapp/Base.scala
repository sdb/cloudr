package be.ellefant.droid.cloudapp

import android.app.Activity
import android.content.{Intent, ContentResolver}
import android.os.Bundle
import android.view.{MenuItem, Menu}

object Base extends sdroid.Types {

  trait Activity extends AActivity with Logging

  trait AccountRequired extends Activity with be.ellefant.droid.cloudapp.AccountRequired {
    protected[cloudapp] def onAccountFailure() = {
      finish()
    }
  }
  
  trait Default extends Activity {
  	self: Activity with Logging with AccountRequired => 
  
	  abstract override def onCreateOptionsMenu(menu: Menu) = {
	    // super.onCreateOptionsMenu(menu)
	    val inflater = getMenuInflater()
	    inflater.inflate(R.menu.main_menu, menu)
	    true
	  }
	  
	  abstract override def onOptionsItemSelected(item: MenuItem) = item.getItemId match {
	    case R.id.sync =>
	      ContentResolver.requestSync(account(), "cloudapp", new Bundle)
	      true
	    case R.id.settings =>
	      val intent = new Intent("android.intent.action.VIEW")
	    	intent.setClass(this, classOf[AccountPreferencesActivity])
	    	startActivity(intent)
	      true
	    case _ => super.onOptionsItemSelected(item)
	  } 
	}

  trait Service extends AService with Logging
}