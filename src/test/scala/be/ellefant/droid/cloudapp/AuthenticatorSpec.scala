package be.ellefant.droid.cloudapp

class AuthenticatorSpec extends CloudrSpecs {

  "Authenticator" should  {

    "trigger AuthenticatorActivity when addAccount is called" in  {
      pending
    }

    "trigger AuthenticatorActivity when confirmCredentials is called without password" in  {
      pending
    }

    "return false when confirmCredentials is called and the credentials are invalid" in  {
      pending
    }

    "return true when confirmCredentials is called and the credentials are valid" in  {
      pending
    }

    "trigger AuthenticatorActivity when getAuthToken is called and no password is available" in  {
      pending
    }

    "return false when getAuthToken is called and the password from the AccountManager is invalid" in  {
      pending
    }

    "return true when getAuthToken is called and the password from the AccountManager is valid" in  {
      pending
    }

    "trigger AuthenticatorActivity when updateCredentials is called" in  {
      pending
    }
  }

  trait context extends RoboContext
      with Mocks.AccountManagerMock {
  }
}