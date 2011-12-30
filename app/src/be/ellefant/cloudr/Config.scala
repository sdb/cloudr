package be.ellefant.cloudr

import java.util.Properties
import com.cloudapp.impl.CloudAppBase.Host
import org.apache.http.client.params.AuthPolicy

class Config {
  private val props = {
    val p = new Properties
    p.load(getClass.getResourceAsStream("app.properties"))
    p.load(getClass.getResourceAsStream("cloudapp.properties"))
    p
  }

  def getInt(key: String, default: Int) = try {
    props.getProperty(key, default.toString).toInt
  }  catch {
    case e => default
  }

  def version = props.getProperty("version", "UNKNOWN")
  def name = props.getProperty("name", "UNKNOWN")

  def cloudAppHost = new Host(
    props.getProperty("cloudapp.scheme", "http"),
    props.getProperty("cloudapp.host", "my.cl.ly"),
    getInt("cloudapp.port", 80),
    props.getProperty("cloudapp.auth", "digest").toLowerCase match {
      case "basic" => AuthPolicy.BASIC
      case _ => AuthPolicy.DIGEST
    }
  )
}