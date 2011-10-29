package be.ellefant.droid.cloudapp.tests

import junit.framework.Assert._
import android.test.ActivityUnitTestCase
import android.app.Activity
import be.ellefant.droid.cloudapp.{Logging, AccountRequired}
import com.jayway.android.robotium.solo.Solo

class AccountRequiredSpy extends Activity with Logging with AccountRequired {
  var success = false
  var failure = false
  def onAccountSuccess(name: String) = success = true
  def onAccountFailure() = failure = true
}

class AccountRequiredTest extends ActivityUnitTestCase[AccountRequiredSpy](classOf[AccountRequiredSpy]) {
  var solo: Solo = _

  def setUp() {
    solo = new Solo(getInstrumentation(), getActivity())
  }

  def tearDown() {

    try {
      solo.finalize()
    } catch {
      case e =>
        e.printStackTrace()
    }
    getActivity().finish()
    super.tearDown()
  }


}