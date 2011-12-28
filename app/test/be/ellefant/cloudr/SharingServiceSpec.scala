package be.ellefant.cloudr

import android.content.Intent
import android.accounts.Account
import com.cloudapp.api.model.CloudAppItem
import android.content.Context
import android.text.ClipboardManager

class SharingServiceSpec extends CloudrSpecs {

  "when an intent is received and no account is available SharingService" should {
    "do nothing" in pending
  }

  "when a bookmark intent is received SharingService" should {
    "make a request to create a bookmark" in pending
    "and when the request succeeds" in {
      "display a toast" in pending
      "save a drop description" in pending
    }
    "and when the request fails" in {
      "display a toast" in pending
      "not save a drop description" in pending
    }
  }

  "when an upload intent is received SharingService" should {
    "make a request to upload a file" in pending
    "and when the request succeeds" in {
      "display a toast" in pending
      "save a drop description" in pending
    }
    "and when the request fails" in {
      "display a toast" in pending
      "not save a drop description" in pending
    }
  }

  "when an item is uploaded the SharingService" should {
    "check the 'copy to clipboard' preference" in {
      "copy the url to the clipboard" in pending
      "not copy the url to the clipboard" in pending
    }
  }

  "when an authentication error occurs the SharingService" should {
    "display a toast" in pending
  }

  "when the upload limit is reached the SharingService" should {
    "display a toast" in pending
  }

  "when some other error occurs the SharingService" should {
    "display a toast" in pending
  }

}