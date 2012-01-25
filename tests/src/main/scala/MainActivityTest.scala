package be.ellefant.cloudr
package tests

import android.test.ActivityInstrumentationTestCase2
import android.widget.ListView
import junit.framework.Assert._

class MainActivityTest extends ActivityInstrumentationTestCase2(classOf[MainActivity])
    with CloudrTestBase {

  override def setUp() {
    super.setUp()
    setupAccount()
  }

  def testPreconditions() {
    val activity = getActivity
    val view = activity.findViewById(android.R.id.list).asInstanceOf[ListView]
    assertNotNull(view)
    assertEquals(10, view.getAdapter.getCount)
  }

}