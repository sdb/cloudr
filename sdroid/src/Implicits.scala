package sdroid

import android.widget.AdapterView
import AdapterView.OnItemClickListener
import android.view.View
import View.OnClickListener

trait Implicits {
  type ItemClickListener = (AdapterView[_], View, Int, Long) => Unit

  implicit def func2onItemClickListener(f: ItemClickListener) = new OnItemClickListener() {
    def onItemClick(p1: AdapterView[_], p2: View, p3: Int, p4: Long) {
      f(p1, p2, p3, p4)
    }
  }
  
  type ClickListener = (View) => Unit

  implicit def func2onClickListener(f: ClickListener) = new OnClickListener() {
    def onClick(p1: View) {
      f(p1)
    }
  }
}

object Implicits extends Implicits