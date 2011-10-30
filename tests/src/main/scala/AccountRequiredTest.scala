//package be.ellefant.droid.cloudapp.tests
//
//import android.test.ActivityUnitTestCase
//import android.app.{Activity, Application}
//import com.jayway.android.robotium.solo.Solo
//import roboguice.RoboGuice
//import roboguice.activity.RoboActivity
//import roboguice.inject.ContextScoped
//import com.google.inject.AbstractModule
//import com.google.inject.util.Modules
//import junit.framework.Assert._
//import com.borachio.junit3.MockFactory
//import be.ellefant.droid.cloudapp.{AccountManager, AccountManagerProvider, Logging, AccountRequired}
//import android.test.suitebuilder.annotation.MediumTest
//
//@ContextScoped
//class AccountRequiredSpy extends RoboActivity with Logging with AccountRequired {
//  var success = false
//  var failure = false
//
//  def onAccountSuccess(name: String) = success = true
//
//  def onAccountFailure() = failure = true
//}
//
//class AccountRequiredTest extends ActivityUnitTestCase[AccountRequiredSpy](classOf[AccountRequiredSpy]) with MockFactory {
//  // var solo: Solo = _
//
//  private val accountManagerMock = mock[AccountManager]
//
//  override def setUp() {
//    val app = getActivity.getApplication
//    RoboGuice.setBaseApplicationInjector(app, RoboGuice.DEFAULT_STAGE, Modules.`override`(RoboGuice.newDefaultRoboModule(app)).`with`(new TestModule()))
//    // solo = new Solo(getInstrumentation(), getActivity())
//  }
//
//  def testSomething() {
//    assertTrue(true)
//  }
//
//  override def tearDown() {
////    try {
////      solo.finalize()
////    } catch {
////      case e =>
////        e.printStackTrace()
////    }
//    getActivity().finish()
//    super.tearDown()
//  }
//
//  class TestModule extends AbstractModule {
//    def configure() {
//      bind(classOf[AccountManager]).toInstance(accountManagerMock)
//    }
//  }
//}