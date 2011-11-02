package be.ellefant.droid.cloudapp

import android.app.Activity

trait BaseActivity extends Activity with Logging

trait AccountRequiredBaseActivity extends BaseActivity with AccountRequired {
  protected[cloudapp] def onAccountFailure() = {
    finish()
  }
}