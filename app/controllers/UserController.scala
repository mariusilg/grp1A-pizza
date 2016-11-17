package controllers

import play.api.mvc.{Action, AnyContent, Controller}
import play.api.data.Form
import play.api.data.Forms.{mapping,text}
import services.UserService
import forms.CreateUserForm
import forms.CreateLoginForm

/**
 * Controller for user specific operations.
 *
 * @author ob, scs
 */
object UserController extends Controller {

  /**
   * Form object for user data.
   */
  val userForm = Form(
    mapping(
      "Name" -> text)(CreateUserForm.apply)(CreateUserForm.unapply))

  val loginForm = Form(
    mapping(
      "userName" -> text
    )(CreateLoginForm.apply)(CreateLoginForm.unapply))

  /**
   * Adds a new user with the given data to the system.
   *
   * @return welcome page for new user
   */
  def addUser : Action[AnyContent] = Action { implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.index(formWithErrors, null, UserService.registeredUsers))
      },
      userData => {
        val newUser = services.UserService.addUser(userData.name)
        Redirect(routes.UserController.welcomeUser(newUser.name)).
          flashing("success" -> "User saved!")
      })
  }


  def login : Action[AnyContent] = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.index(null, formWithErrors, UserService.registeredUsers))
      },
      userData => {
        val user = services.UserService.getUser(userData.userName).get
        if (user.admin) {
          Redirect(routes.UserController.welcomeAdmin(user.name)).
          flashing("success" -> "successfully logged in as admin!")
      } else {
          Redirect(routes.UserController.welcomeUser(user.name)).
            flashing("success" -> "successfully logged in as customer!")
        }
      })
  }

  /**
   * Shows the welcome view for a newly registered user.
   */
  def welcomeUser(username: String) : Action[AnyContent] = Action {
    Ok(views.html.welcomeUser(username))
  }

  def welcomeAdmin(name : String) : Action[AnyContent] = Action {
    val user = services.UserService.getUser(name).get
    Ok(views.html.welcomeAdmin(){user})
  }

  /**
   * List all users currently available in the system.
   */
  def showUsers : Action[AnyContent] = Action {
    Ok(views.html.users(UserService.registeredUsers))
  }

}
