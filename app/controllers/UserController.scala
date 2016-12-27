package controllers

import play.api.mvc.{Action, AnyContent, Controller}
import play.api.data.Form
import play.api.data.Forms._ //{mapping,text,number}
import services._
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
      "Name" -> text.verifying("Please specify a name", f => f.trim!="").verifying("Username existiert bereits", name => !services.UserService.checkName(name)),
      "Password" -> text.verifying("Please specify a password", f => f.trim!=""),
      "Admin" -> optional(boolean)
    )(CreateUserForm.apply)(CreateUserForm.unapply))



  val editUserForm = Form(
    mapping(
      "Id" -> longNumber,
      "Name" -> text.verifying("Please specify a name", f => f.trim!=""),
      //"Password" -> text,
      "Admin" -> boolean
    )(EditUserForm.apply)(EditUserForm.unapply))


  val loginForm = Form(
    mapping(
      "Username" -> text.verifying("Username existiert nicht", name => services.UserService.checkName(name)),
      "Password" -> text
    )(CreateLoginForm.apply)(CreateLoginForm.unapply))

  /**
   * Adds a new user with the given data to the system.
   *
   * @return welcome page for new user
   */
  def register : Action[AnyContent] = Action { implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.index(formWithErrors, controllers.UserController.loginForm, UserService.registeredUsers))
      },
      userData => {
        println("checkbox:" + userData.admin.getOrElse(false))
          val newUser = services.UserService.addUser(userData.name, userData.password, false)
          Redirect(routes.UserController.welcome(None)) withSession("id" -> newUser.id.toString)
      })
  }

  /**
    * Adds a new user with the given data to the system.
    *
    * @return welcome page for new user
    */
  def addUser : Action[AnyContent] = Action { implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.users(formWithErrors, UserService.registeredUsers, List("OK", "Gut")))
      },
      userData => {
        println("checkbox:" + userData.admin.getOrElse(false))
        val newUser = services.UserService.addUser(userData.name, userData.password, userData.admin.getOrElse(false))
        Redirect(routes.UserController.manageUser)
      })
  }


  def login : Action[AnyContent] = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.index(userForm, formWithErrors, UserService.registeredUsers))
      },
      userData => {
        val id = services.UserService.login(userData.userName, userData.userPassword)
        id match {
          case Some(id) => Redirect(routes.UserController.welcome(None)) withSession("id" -> id.toString) //.flashing("success" -> "Login verified!")
          case None => Forbidden("I don’t know you")
        }
      })
  }

  /**
   * Shows the welcome view for a (newly) registered user.
   */
  def welcome(categoryID: Option[Long]) : Action[AnyContent] = Action { request =>
      request.session.get("id").map { id =>
        val user = services.UserService.getUserByID(id.toLong)
        user match {
          case Some(user) => if (user.admin) {Ok(views.html.welcomeAdmin(user, UserService.registeredUsers))}
          else {
            val category = services.CategoryService.getCategory(categoryID.getOrElse(1))
            category match {
              case Some(category) => Ok(views.html.welcomeUser(controllers.OrderController.orderForm, user, category.id))
              case None => Redirect(routes.UserController.welcome(None))
            }
          }
          case None => Redirect(routes.Application.index)
        }
      }.getOrElse {
        Redirect(routes.Application.index)
      }
  }

  /**
   * List all users currently available in the system.
   */
  def showUsers() : Action[AnyContent] = Action {
    Ok(views.html.users(userForm, UserService.registeredUsers, List("showUsers success")))
  }

  /**
    * Manage all users which are currently available in the system.
    */
  def manageUser() : Action[AnyContent] = Action { request =>
    request.session.get("id").map { id =>
      Ok(views.html.users(userForm, UserService.registeredUsers, List("manageUser success")))
    }.getOrElse {
      Redirect(routes.Application.index)
    }
  }

  /**
    * Edit a specific user.
    */
  def editUser(ofUser: Option[Long]) : Action[AnyContent] = Action { request =>
    request.session.get("id").map { id =>
      val currentUser = UserService.getUserByID(id.toLong)
      currentUser match {
        case Some(currentUser) => ofUser match {
                                case Some(ofUser) => val user = UserService.getUserByID(ofUser)
                                                      user match {
                                                        case Some(user) => if(currentUser.admin) Ok(views.html.editUser(true, user)) else Redirect(routes.UserController.editUser(None))
                                                        case None => if(currentUser.admin) Redirect(routes.UserController.manageUser) else Redirect(routes.UserController.editUser(None))
                                                      }
                                case None => Ok(views.html.editUser(currentUser.admin, currentUser))
                                }
        case None => Redirect(routes.Application.index)
      }
    }.getOrElse {
      Redirect(routes.Application.index)
    }
  }

  /**
    * Update a specific User and go back to user overview.
    */
  def updateUser() : Action[AnyContent] = Action { implicit request =>
    editUserForm.bindFromRequest.fold(
      formWithErrors => {
        Forbidden("Fehler")//BadRequest(views.html.editUser(None))
      },
      userData => {
        val user = models.User(userData.id, userData.name, "password", userData.admin)//, userData.password, userData.admin)
        UserService.updateUser(user)
        Redirect(routes.UserController.editUser(Some(user.id)))//"Successfully updated changes."
      })
  }

  def logout() : Action[AnyContent] = Action {
    Redirect(routes.Application.index).withNewSession
  }

}
