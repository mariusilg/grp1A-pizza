package forms

/**
  * Form containing data to create a login.
  * @param userName name of the user.
  * @param userPassword password of the user.
  */
case class CreateLoginForm(userName: String, userPassword: String)