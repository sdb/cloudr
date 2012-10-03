package scalaandroid

import android.view.View
import android.widget.AdapterView

trait AdapterViewImplicits {
  import AdapterViewImplicits._
	
  implicit def func2onItemClickListener(f: OnItemClickListener) = new AdapterView.OnItemClickListener() {
    def onItemClick(parent: AdapterView[_], view: View, position: Int, id: Long) {
      f(parent, view, position, id)
    }
  }
}

object AdapterViewImplicits extends AdapterViewImplicits {
  type OnItemClickListener = (AdapterView[_], View, Int, Long) => Unit
}