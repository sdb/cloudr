package be.ellefant.droid.cloudapp.tests

import junit.framework.Assert._
import android.test.AndroidTestCase

class AccountRequiredTest extends AndroidTestCase {
  def testPackageIsCorrect {
    assertEquals("be.ellefant.droid.cloudapp", getContext.getPackageName)
  }
}