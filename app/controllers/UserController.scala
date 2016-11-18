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


  //val nonEmptyAlphaText: Mapping[String] = nonEmptyText.verifying("Must contain letters and spaces only.", name => name.matches("[A-z\\s]+") )
  /**
   * Form object for user data.
   */
  val userForm = Form(
    mapping(
      "Name" -> text.verifying(
        "Please specify a name", f => f.trim!=""))(CreateUserForm.apply)(CreateUserForm.unapply))

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
        BadRequest(views.html.index(formWithErrors, controllers.UserController.loginForm, UserService.registeredUsers))
      },
      userData => {
          val newUser = services.UserService.addUser(userData.name)
          Redirect(routes.UserController.welcomeUser(newUser.name)).
            flashing("success" -> "User saved!")
      //}
      })
  }


  def login : Action[AnyContent] = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.index(controllers.UserController.userForm, formWithErrors, UserService.registeredUsers))
      },
      userData => {
        val user = services.UserService.getUser(userData.userName)
        user match {
          case Some(user) => Redirect(routes.UserController.welcomeUser(user.name)).
            flashing("success" -> "Login verified!")
          case None => Forbidden("I don’t know you")
        }
      })
  }

  /**
   * Shows the welcome view for a newly registered user.
   */
  def welcomeUser(username: String) : Action[AnyContent] = Action {
    val user = services.UserService.getUser(username)
    user match {
      case Some(user) => if (user.admin) {
        Ok(views.html.welcomeAdmin(){user})
      } else {
        Ok(views.html.welcomeUser(username))
      }
      case None => Forbidden("I don’t know you")
    }

  }

  /**
   * List all users currently available in the system.
   */
  def showUsers : Action[AnyContent] = Action {
    Ok(views.html.users(UserService.registeredUsers))
  }

}
