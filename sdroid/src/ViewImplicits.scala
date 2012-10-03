package scalaandroid

import android.view.View

trait ViewImplicits {
  import ViewImplicits._
	
  implicit def func2onClickListener(f: OnClickListener) = new View.OnClickListener {
    def onClick(view: View) { f(view) }
  }
}

object ViewImplicits extends ViewImplicits {
  type OnClickListener = (View) => Unit
}