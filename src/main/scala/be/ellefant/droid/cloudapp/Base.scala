package be.ellefant.droid.cloudapp

import android.app.{Service => AService, Activity => AActivity}

object Base {
  trait Activity extends AActivity with Logging

  trait AccountRequired extends Activity with be.ellefant.droid.cloudapp.AccountRequired {
    protected[cloudapp] def onAccountFailure() = {
      finish()
    }
  }

  trait Service extends AService with Logging
}