package be.ellefant.cloudr

import org.specs2.mutable._
import org.specs2.mock.Mockito
import com.github.jbrechtel.robospecs.RoboSpecs
import roboguice.RoboGuice
import com.xtremelabs.robolectric.{ Robolectric, RobolectricConfig }
import com.google.inject.AbstractModule
import com.google.inject.util.Modules
import android.accounts.AccountManager

trait CloudrSpecs extends RoboSpecs with RoboGuiceSpecification {
  args(sequential = true)

  trait RoboContext extends RoboContextBase with After
}