//package be.ellefant.droid.cloudapp
//
//import roboguice.application.RoboApplication
//import com.google.inject.Module
//import android.content.Context
//
//class CloudAppApplication extends RoboApplication {
//  def this(context: Context) = {
//    this()
//    attachBaseContext(context)
//  }
//  override def addApplicationModules(modules: java.util.List[Module]) {
//    modules.add(new CloudAppModule)
//  }
//
//}