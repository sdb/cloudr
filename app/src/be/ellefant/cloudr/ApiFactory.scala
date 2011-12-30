package be.ellefant.cloudr

import org.apache.http.params.CoreProtocolPNames
import ApiFactory._

class ApiFactory(config: Config) {
  def create(name: String, password: String): Cloud = new Cloud(new CloudAppImpl(config, name, password))
}

object ApiFactory {
  class CloudAppImpl(config: Config, mail: String, pw: String) extends com.cloudapp.impl.CloudAppImpl(mail, pw, config.cloudAppHost) {
    override protected def createClient = {
      val c = super.createClient
      val p = c.getParams
      val appId = "%s/%s" format (config.name, config.version)
      val ua = appId + (Option(p.getParameter(CoreProtocolPNames.USER_AGENT)) flatMap (_.toString.toBlankOption) map (" " + _.toString) getOrElse (""))
      p.setParameter(CoreProtocolPNames.USER_AGENT, ua)
      c.setParams(p)
      c
    }
  }
}