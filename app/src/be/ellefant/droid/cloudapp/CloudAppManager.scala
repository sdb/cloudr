package be.ellefant.droid.cloudapp

class CloudAppManager extends Injection.Resources {
  def itemTypes: Array[String] = resources.getStringArray(R.array.item_types)
}