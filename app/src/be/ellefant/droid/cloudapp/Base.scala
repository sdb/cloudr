package be.ellefant.droid.cloudapp

object Base extends sdroid.Types {

  trait Activity extends AActivity with Logging

  trait AccountRequired extends Activity with be.ellefant.droid.cloudapp.AccountRequired {
    protected[cloudapp] def onAccountFailure() = {
      finish()
    }
  }

  trait Service extends AService with Logging
}