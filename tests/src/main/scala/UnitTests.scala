package be.ellefant.cloudr
package tests

import junit.framework.Assert._
import android.test.AndroidTestCase

class UnitTests extends AndroidTestCase {

  def testPackageIsCorrect {
    assertEquals("be.ellefant.cloudr", getContext.getPackageName)
  }
}