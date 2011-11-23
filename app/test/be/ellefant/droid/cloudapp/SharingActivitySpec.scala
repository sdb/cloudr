package be.ellefant.droid.cloudapp

import android.accounts.Account
import android.content.Intent
import com.xtremelabs.robolectric.shadows.ShadowToast

class SharingActivitySpec extends CloudrSpecs {
  
  trait context extends RoboContext
      with Bindings.ThreadUtilBinding
      with Mocks.AccountManagerMock
      with Mocks.CloudAppMock {
    
    "SharingActivity" should {
      "when an account is available" in pending
    }
  }

}