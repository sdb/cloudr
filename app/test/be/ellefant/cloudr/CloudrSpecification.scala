package be.ellefant.cloudr

import com.github.jbrechtel.robospecs.RoboAcceptanceSpecs
import org.specs2.mock.Mockito
import roboguice.RoboGuice
import com.xtremelabs.robolectric.{Robolectric, RobolectricConfig}
import android.accounts.AccountManager
import org.specs2.specification.After
import com.google.inject.AbstractModule
import com.google.inject.util.Modules

trait CloudrSpecification extends RoboAcceptanceSpecs with RoboGuiceSpecification {
  trait RoboContext extends RoboContextBase with After
}
