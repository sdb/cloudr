package be.ellefant.droid.cloudapp

import java.util.Properties

class Config {
  private val props = {
    val p = new Properties
    p.load(getClass.getResourceAsStream("app.properties"))
    p
  }
  
  def version = props.getProperty("version", "UNKNOWN")
  def name = props.getProperty("name", "UNKNOWN")
}