package be.ellefant.droid.cloudapp
package tests

import org.specs2.specification.Context
import roboguice.RoboGuice
import com.xtremelabs.robolectric.Robolectric
import com.google.inject.AbstractModule
import com.google.inject.util.Modules
import org.specs2.mutable.After

trait Robo extends After {
  RoboGuice.setBaseApplicationInjector(Robolectric.application, RoboGuice.DEFAULT_STAGE,
    Modules.`override`(RoboGuice.newDefaultRoboModule(Robolectric.application)).`with`(module))
  def after = RoboGuice.util.reset()
  def module: AbstractModule
}