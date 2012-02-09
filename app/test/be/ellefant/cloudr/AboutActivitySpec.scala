package be.ellefant.cloudr

class AboutActivitySpec  extends CloudrSpecification { def is = sequential ^
  "AboutActivity should" ^
    "show the about info" ! Context().showAbout
  end

  case class Context() extends RoboContext with Mocks.ConfigMock {
    val activity = new AboutActivity
    
    def showAbout = this {
      val version = "TEST"
      configMock.version returns version
      activity onCreate null
      activity.getTitle must be_==("About Cloudr " + version)
    }
  }
}
