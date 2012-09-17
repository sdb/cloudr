package scalaandroid

import android.view.{ MenuItem => AMenuItem }

object MenuItem {
	def unapply(i: AMenuItem) = Some(i.getItemId)
}