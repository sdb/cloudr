package scalaandroid

import android.app.{ Activity => AActivity }
import android.view.{ Menu, MenuItem => AMenuItem }

import collection.mutable.ListBuffer
  
trait Activity {
  self: AActivity =>
    
  type OptionsMenuCallback = Menu => Unit  
  lazy val optionsMenuCallbacks = new ListBuffer[OptionsMenuCallback]
  
  def optionsMenu(f: OptionsMenuCallback) = optionsMenuCallbacks += f
    
  override def onCreateOptionsMenu(menu: Menu) = {
    optionsMenuCallbacks foreach (_(menu))
    optionsMenuCallbacks.nonEmpty
  }
  
  type OptionsItemSelectedCallback = PartialFunction[AMenuItem, Unit]
  
  lazy val optionsItemSelectedCallbacks = new ListBuffer[OptionsItemSelectedCallback]
  
  def optionsItemSelected(f: OptionsItemSelectedCallback): Unit =
    optionsItemSelectedCallbacks += f
  
  override def onOptionsItemSelected(item: AMenuItem) =
    (optionsItemSelectedCallbacks reduceLeft (_ orElse _)) lift (item) isDefined

}