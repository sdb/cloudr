package be.ellefant.droid.cloudapp

import android.content.res.Resources
import com.google.inject.Inject

class CloudAppManager {
  @Inject var resources: Resources = _
  def itemTypes: Array[String] = resources.getStringArray(R.array.item_types)
}