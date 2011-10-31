package be.ellefant.droid.cloudapp
package tests

import android.test.ActivityUnitTestCase
import roboguice.RoboGuice
import com.google.inject.AbstractModule
import com.google.inject.util.Modules
import be.ellefant.droid.cloudapp.AccountManager
import junit.framework.Assert._
import org.easymock.EasyMock
class MainActivityTest extends ActivityUnitTestCase[MainActivity](classOf[MainActivity]) {
  private val accountManagerMock = EasyMock.createMock(classOf[AccountManager])

  override def setUp() {
    super.setUp()
    val app = new TestApplication(getInstrumentation().getTargetContext())
    setApplication(app)
    RoboGuice.setBaseApplicationInjector(app, RoboGuice.DEFAULT_STAGE, Modules.`override`(RoboGuice.newDefaultRoboModule(app)).`with`(new TestModule()))
  }

  def testSomething() {
    assertTrue(true)
  }

  override def tearDown() {
    getActivity().finish()
    super.tearDown()
  }

  class TestModule extends AbstractModule {
    def configure() {
      bind(classOf[AccountManager]).toInstance(accountManagerMock)
    }
  }
}