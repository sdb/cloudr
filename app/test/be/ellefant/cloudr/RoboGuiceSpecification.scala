package be.ellefant.cloudr

import com.github.jbrechtel.robospecs.RoboAcceptanceSpecs
import org.specs2.mock.Mockito
import roboguice.RoboGuice
import com.xtremelabs.robolectric.{Robolectric, RobolectricConfig}
import android.accounts.AccountManager
import org.specs2.specification.After
import com.google.inject.AbstractModule
import com.google.inject.util.Modules
import com.github.jbrechtel.robospecs.RoboSpecsWithInstrumentation

trait RoboGuiceSpecification extends RoboSpecsWithInstrumentation with Mockito with Logging {

  override lazy val robolectricConfig = new RobolectricConfig(new java.io.File("./app/"))

  trait RoboContextBase {
    RoboGuice.setBaseApplicationInjector(Robolectric.application, RoboGuice.DEFAULT_STAGE,
      Modules.`override`(RoboGuice.newDefaultRoboModule(Robolectric.application)).`with`(module))

    def after = RoboGuice.util.reset()

    def configure() = {}

    object module extends AbstractModule {
      def configure() {
        RoboContextBase.this.configure()
      }
      override def bind[T](c: Class[T]) = super.bind(c)
    }
  }

  object Mocks {
    trait AccountManagerMock extends RoboContextBase {
      lazy val accountManagerMock = mock[AccountManager]
      abstract override def configure() {
        super.configure()
        module.bind(classOf[AccountManager]).toInstance(accountManagerMock)
      }
    }

    trait CloudAppMock extends RoboContextBase {
      lazy val cloudAppMock = mock[Cloud]
      abstract override def configure() {
        super.configure()
        module.bind(classOf[ApiFactory]).toInstance(new ApiFactory(null) {
          override def create(name: String, password: String) = cloudAppMock
        })
      }
    }

    trait CloudAppManagerMock extends RoboContextBase {
      lazy val cloudAppManagerMock = mock[CloudAppManager]
      abstract override def configure() {
        super.configure()
        module.bind(classOf[CloudAppManager]).toInstance(cloudAppManagerMock)
      }
    }

    trait DropManagerMock extends RoboContextBase {
      lazy val dropManagerMock = mock[DropManager]
      abstract override def configure() {
        super.configure()
        module.bind(classOf[DropManager]).toInstance(dropManagerMock)
      }
    }

    trait ConfigMock extends RoboContextBase {
      lazy val configMock = mock[Config]
      abstract override def configure() {
        super.configure()
        module.bind(classOf[Config]).toInstance(configMock)
      }
    }
  }
}
