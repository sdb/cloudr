package be.ellefant.droid.cloudapp

import com.cloudapp.api.CloudApp
import com.cloudapp.impl.CloudAppImpl

class ApiFactory {
  def create(name: String, password: String): CloudApp = new CloudAppImpl(name, password)
}