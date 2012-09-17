package scalaandroid

import android.content.{ Intent => AIntent, Context }

object Intent {
	def unapply(intent: AIntent) = Some(intent.getAction)
	def apply(packageContext: Context, cls: Class[_]) = new AIntent(packageContext, cls)
}