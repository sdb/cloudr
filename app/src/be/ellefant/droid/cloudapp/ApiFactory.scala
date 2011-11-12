package be.ellefant.droid.cloudapp

import com.cloudapp.api.CloudApp
import org.apache.http.params.CoreProtocolPNames
import ApiFactory._

class ApiFactory {
  def create(name: String, password: String): CloudApp = new CloudAppImpl(name, password)
}

object ApiFactory {
  class CloudAppImpl(mail: String, pw: String) extends com.cloudapp.impl.CloudAppImpl(mail, pw) {
    override protected def createClient = {
      val c = super.createClient
      val p = c.getParams
      //val ua = "Cloudr/0.0.1" + (p.getParameter(CoreProtocolPNames.USER_AGENT).toBlankOption map (" " + _.toString) getOrElse (""))
      val ua = "Cloudr/0.0.1" + (Option(p.getParameter(CoreProtocolPNames.USER_AGENT)) flatMap (_.toString.toBlankOption) map (" " + _.toString) getOrElse (""))
      p.setParameter(CoreProtocolPNames.USER_AGENT, ua)
      c.setParams(p)
      c
    }
  }
}