package be.ellefant.cloudr

class DropActivitySpec extends CloudrSpecs {

  "DropActivity" should {
    "show the drop details" in new Context {
      activity onCreate null
    }
  }

  trait Context extends RoboContext
      with Mocks.AccountManagerMock
      with Mocks.CloudAppMock
      with Mocks.DropManagerMock {

    val activity = new DropActivity
  }

}
