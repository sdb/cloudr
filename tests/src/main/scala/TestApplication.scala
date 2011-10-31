package be.ellefant.droid.cloudapp
package tests

import android.content.Context
import android.app.Application

class TestApplication(context: Context) extends Application {
  attachBaseContext(context)
}