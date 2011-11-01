package be.ellefant.droid.cloudapp

import android.app.Activity
import android.os.Bundle
import android.accounts.{AccountManagerFuture, Account}
import android.content.Context
import com.google.inject.{Inject, Provider}
import roboguice.inject.ContextScoped

trait AccountManager {
  def getAccountsByType(t: String): Seq[Account]
  def addAccount(accountType: String, authTokenType: String, activity: Activity): AccountManagerFuture[Bundle]
  def setPassword(account: Account, password: String)
  def getPassword(account: Account): String
  def addAccountExplicitly(account: Account, password: String)
}

class AccountManagerImpl(am: android.accounts.AccountManager) extends AccountManager {
  def getAccountsByType(t: String): Seq[Account] = am.getAccountsByType(t)
  def addAccount(accountType: String, authTokenType: String, activity: Activity): AccountManagerFuture[Bundle] =
    am.addAccount(accountType, authTokenType, null, null, activity, null, null)
  def setPassword(account: Account, password: String) = am.setPassword(account, password)
  def getPassword(account: Account) = am.getPassword(account)
  def addAccountExplicitly(account: Account, password: String) = am.addAccountExplicitly (account , password, null)
}

@ContextScoped
class AccountManagerProvider extends Provider[AccountManager] {
  @Inject protected var context: Context = _
  def get(): AccountManager = new AccountManagerImpl(android.accounts.AccountManager.get(context))
}