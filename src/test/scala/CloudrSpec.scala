package be.ellefant.droid.cloudapp

import org.specs2.mutable._
import org.specs2.mock.Mockito
import com.github.jbrechtel.robospecs.RoboSpecs
import roboguice.RoboGuice
import com.xtremelabs.robolectric.Robolectric
import com.google.inject.AbstractModule
import com.google.inject.util.Modules

trait CloudrSpec extends RoboSpecs with Mockito {
  args(sequential=true)

  trait Robo extends After {
    RoboGuice.setBaseApplicationInjector(Robolectric.application, RoboGuice.DEFAULT_STAGE,
      Modules.`override`(RoboGuice.newDefaultRoboModule(Robolectric.application)).`with`(module))
    def after = RoboGuice.util.reset()
    def module: AbstractModule
  }
}