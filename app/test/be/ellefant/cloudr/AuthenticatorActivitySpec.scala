package be.ellefant.cloudr

class AuthenticatorActivitySpec extends CloudrSpecs {

  "AuthenticatorActivity" should {
    "display a toast when an account is already available" in {
      pending
    }
    "display the login when no account is available" in {
      pending
    }
  }

  trait context extends RoboContext
      with Mocks.AccountManagerMock {
    // lazy val authenticator
  }
}