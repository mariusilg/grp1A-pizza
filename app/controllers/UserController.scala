package controllers

import play.api.mvc.{Action, AnyContent, Controller}
import play.api.data.Form
import play.api.data.Forms._ //{mapping,text,number}
import services.UserService
import forms._

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

  val orderForm = Form(
    mapping(
      "itemID" -> longNumber)(CreateOrderForm.apply)(CreateOrderForm.unapply))

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
          Redirect(routes.UserController.welcomeUser(newUser.name, 1)).
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
          case Some(user) => Redirect(routes.UserController.welcomeUser(user.name, 1)).
            flashing("success" -> "Login verified!")
          case None => Forbidden("I don’t know you")
        }
      })
  }

  /**
   * Shows the welcome view for a (newly) registered user.
   */
  def welcomeUser(username: String, cID: Long) : Action[AnyContent] = Action {
    val user = services.UserService.getUser(username)
    user match {
      case Some(user) => if (user.admin) {Ok(views.html.welcomeAdmin(user, UserService.registeredUsers))}
                        else {
                            val category = services.UserService.getCategory(cID)
                            category match {
                              case Some(category) => Ok(views.html.welcomeUser(controllers.UserController.orderForm, username, cID))
                              case None => Forbidden("I don’t know this Category")
                            }
                        }
      case None => Forbidden("I don’t know you - Please try again to log in")
    }
  }

  def addOrder : Action[AnyContent] = Action { implicit request =>
    orderForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.welcomeUser(formWithErrors, "Customer", 1))
      },
      userData => {
        val order = services.UserService.addOrder(2, userData.itemID, 1, 1)
        Redirect(routes.UserController.welcomeUser("Customer", 1)).
          flashing("success" -> "User saved!")
        //}
      })
  }

  /**
   * List all users currently available in the system.
   */
  def showUsers : Action[AnyContent] = Action {
    Ok(views.html.users(UserService.registeredUsers))
  }

}
