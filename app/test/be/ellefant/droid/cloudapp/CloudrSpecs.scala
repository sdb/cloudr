package be.ellefant.droid.cloudapp

import org.specs2.mutable._
import org.specs2.mock.Mockito
import com.github.jbrechtel.robospecs.RoboSpecs
import roboguice.RoboGuice
import com.xtremelabs.robolectric.{ Robolectric, RobolectricConfig }
import com.google.inject.AbstractModule
import com.google.inject.util.Modules
import android.accounts.AccountManager

trait CloudrSpecs extends RoboSpecs with Mockito {
  args(sequential = true)

  override lazy val robolectricConfig = new RobolectricConfig(new java.io.File("./app/"))

  trait RoboContext extends After {
    RoboGuice.setBaseApplicationInjector(Robolectric.application, RoboGuice.DEFAULT_STAGE,
      Modules.`override`(RoboGuice.newDefaultRoboModule(Robolectric.application)).`with`(module))

    def after = RoboGuice.util.reset()

    def configure() = {}

    object module extends AbstractModule {
      def configure() {
        RoboContext.this.configure()
      }
      override def bind[T](c: Class[T]) = super.bind(c)
    }
  }

  object Mocks {
    trait AccountManagerMock extends RoboContext {
      lazy val accountManagerMock = mock[AccountManager]
      abstract override def configure() {
        super.configure()
        module.bind(classOf[AccountManager]).toInstance(accountManagerMock)
      }
    }

    trait CloudAppMock extends RoboContext {
      lazy val cloudAppMock = mock[Cloud]
      abstract override def configure() {
        super.configure()
        module.bind(classOf[ApiFactory]).toInstance(new ApiFactory(null) {
          override def create(name: String, password: String) = cloudAppMock
        })
      }
    }

    trait CloudAppManagerMock extends RoboContext {
      lazy val cloudAppManagerMock = mock[CloudAppManager]
      abstract override def configure() {
        super.configure()
        module.bind(classOf[CloudAppManager]).toInstance(cloudAppManagerMock)
      }
    }
  }

  object Bindings {
    trait ThreadUtilBinding extends RoboContext {
      abstract override def configure() {
        super.configure()
        module.bind(classOf[ThreadUtil]).toInstance(new ThreadUtil {
          override def performOnBackgroundThread(r: Runnable) = { r.run(); null }
        })
      }
    }
  }
}