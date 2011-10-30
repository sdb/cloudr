package be.ellefant.droid.cloudapp

import roboguice.inject.ContextScoped
import android.content.Context
import com.google.inject.{Inject, Provider}

@ContextScoped
class AccountManagerProvider extends Provider[AccountManager] {

  @Inject protected var context: Context = _

  def get(): AccountManager = new AccountManagerImpl(android.accounts.AccountManager.get(context))
}
