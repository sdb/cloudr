package be.ellefant.cloudr

import ConnectivityRequired._
import android.content.Context
import android.preference.PreferenceManager
import android.net.wifi.WifiManager
import android.widget.Toast
import android.os.Handler
import ThreadUtils._

trait ConnectivityRequired { self: Context with ConnectivityRequiredComponent with Logging =>
  def uploadAllowed(handler: Handler)(f: () => Any) = {
    def withHandler(f: () => Any) = {
      if (handler != null)
        handler post { () => f() }
      else
        f()
    }
    val sharedPrefs = PreferenceManager getDefaultSharedPreferences this
    val onlyWifi = sharedPrefs getBoolean ("only_wifi", false)
    val wifiManager = getSystemService(Context.WIFI_SERVICE).asInstanceOf[WifiManager]
    if (onlyWifi && !wifiManager.isWifiEnabled) {
      withHandler { () ⇒
        val msg = "CloudApp upload is not possible because there's no Wi-Fi connection available."
        val toast = Toast makeText (getApplicationContext, msg, Toast.LENGTH_SHORT)
        toast show ()
      }
    } else {
      f()
    }
  }
}

object ConnectivityRequired {
  trait ConnectivityRequiredComponent {
    def getSystemService(s: String): Any
  }
}