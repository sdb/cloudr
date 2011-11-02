package sdroid

import android.widget.AdapterView.OnItemClickListener
import android.widget.AdapterView
import android.view.View

trait Implicits {
  type ItemClickListener = (AdapterView[_], View, Int, Long) => Unit

  implicit def func2onItemClickListener(f: ItemClickListener) = new OnItemClickListener() {
    def onItemClick(p1: AdapterView[_], p2: View, p3: Int, p4: Long) {
      f(p1, p2, p3, p4)
    }
  }
}

object Implicits extends Implicits