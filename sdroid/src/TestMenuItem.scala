package sdroid.robolectric

object TestMenuItem {
  def apply(itemId: Int) = new com.xtremelabs.robolectric.tester.android.view.TestMenuItem {
    override def getItemId = itemId
  }
}
