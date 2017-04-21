package forms

/**
  * Form containing data to create a login request.
  * @param userName name of the user.
  * @param userPassword password of the user.
  */
case class LoginForm(userName: String, userPassword: String)