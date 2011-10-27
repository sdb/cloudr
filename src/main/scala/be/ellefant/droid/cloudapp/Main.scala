package be.ellefant.droid.cloudapp

import android.os.Bundle
import android.widget.TextView
import Main._

class Main extends BaseActivity {
  val tag = Tag

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(new TextView(this) {
      setText("Hello!")
    })
  }
}

object Main {
  val Tag = classOf[Main].getSimpleName
}