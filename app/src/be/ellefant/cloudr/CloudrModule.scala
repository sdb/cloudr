package be.ellefant.cloudr

import android.accounts.AccountManager
import android.content.Context
import com.google.inject._
import roboguice.inject.ContextScoped
import CloudrModule._

/**
 * Guice module for Cloudr.
 */
class CloudrModule extends AbstractModule {

  def configure() = {
    val config = new Config
    bind(classOf[AccountManager]).toProvider(classOf[AccountManagerProvider])
    bind(classOf[ThreadUtil]).toInstance(new ThreadUtil)
    bind(classOf[ApiFactory]).toInstance(new ApiFactory(config))
    bind(classOf[CloudAppManager]).toInstance(new CloudAppManager)
    bind(classOf[DropManager]).toProvider(classOf[DropManagerProvider])
    bind(classOf[Config]).toInstance(config)
  }
}

object CloudrModule {

  @ContextScoped
  class AccountManagerProvider extends Provider[AccountManager] {
    @Inject protected var context: Context = _
    def get(): AccountManager = android.accounts.AccountManager.get(context)
  }

  @ContextScoped
  class DropManagerProvider extends Provider[DropManager] {
    @Inject protected var context: Context = _
    def get(): DropManager = new DropManager(context)
  }
}