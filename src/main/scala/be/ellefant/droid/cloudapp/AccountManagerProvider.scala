package be.ellefant.droid.cloudapp

import roboguice.inject.ContextScoped
import android.accounts.AccountManager
import android.content.Context
import com.google.inject.{Inject, Provider}

@ContextScoped
class AccountManagerProvider extends Provider[AccountManager] {

  @Inject protected var context: Context = _

  def get() = AccountManager.get(context)
}
