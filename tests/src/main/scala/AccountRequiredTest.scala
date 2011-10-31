package be.ellefant.droid.cloudapp
package tests

import android.test.ActivityUnitTestCase
import roboguice.RoboGuice
import roboguice.activity.RoboActivity
import roboguice.inject.ContextScoped
import com.google.inject.AbstractModule
import com.google.inject.util.Modules
import junit.framework.Assert._
import org.easymock.EasyMock

@ContextScoped
class AccountRequiredSpy extends RoboActivity with Logging with AccountRequired {
  var success = false
  var failure = false

  def onAccountSuccess(name: String) = success = true

  def onAccountFailure() = failure = true
}

class AccountRequiredTest extends ActivityUnitTestCase[AccountRequiredSpy](classOf[AccountRequiredSpy]) {
  private val accountManagerMock = EasyMock.createMock(classOf[AccountManager])

  override def setUp() {
    val app = new TestApplication(getInstrumentation().getTargetContext())
    setApplication(app)
    RoboGuice.setBaseApplicationInjector(app, RoboGuice.DEFAULT_STAGE, Modules.`override`(RoboGuice.newDefaultRoboModule(app)).`with`(new TestModule()))
    // solo = new Solo(getInstrumentation(), getActivity())
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