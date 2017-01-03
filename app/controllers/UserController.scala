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
      "Name" -> text.verifying("Please specify a name", f => f.trim!="").verifying("Username existiert bereits", name => !services.UserService.nameInUse(name)),
      "Password" -> text.verifying("Passwort fehlt!", f => f.trim!=""),
      "Admin" -> optional(boolean),
      "Distance" -> number.verifying("Distanz fehlt!", f => f != null)
    )(CreateUserForm.apply)(CreateUserForm.unapply))



  val editUserForm = Form(
    mapping(
      "ID" -> longNumber,
      "Name" -> text.verifying("Name fehlt", f => f.trim!=""),
      "Password" -> text.verifying("Passwort fehlt", f => f.trim!=""),
      "Admin" -> boolean,
      "Distance" -> number
    )(EditUserForm.apply)(EditUserForm.unapply) /*verifying("Username existiert bereits", fields => fields match {
      case userData => userData.id == userData.id
    })*/
  )


  val loginForm = Form(
    mapping(
      "Username" -> text.verifying("Username fehlt", f => f.trim!="").verifying("Username existiert nicht", name => services.UserService.nameInUse(name)),
      "Password" -> text.verifying("Passwort fehlt", f => f.trim!="")
    )(CreateLoginForm.apply)(CreateLoginForm.unapply))

  /**
   * Adds a new user with the given data to the system.
   *
   * @return welcome page for new user
   */
  def register : Action[AnyContent] = Action { implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.registration(formWithErrors))
      },
      userData => {
          val newUser = services.UserService.addUser(userData.name, userData.password, false, userData.distance)
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
        BadRequest(views.html.users(formWithErrors, UserService.registeredUsers))
      },
      userData => {
        val newUser = services.UserService.addUser(userData.name, userData.password, userData.admin.getOrElse(false), userData.distance)
        Redirect(routes.UserController.manageUser)
      })
  }


  def login : Action[AnyContent] = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.index(formWithErrors))
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
    * Manage all users which are currently available in the system.
    */
  def manageUser() : Action[AnyContent] = Action { implicit request =>
    request.session.get("id").map { id =>
      val user = services.UserService.getUserByID(id.toLong)
      user match {
        case Some(user) => Ok(views.html.users(userForm, UserService.registeredUsers))
        case None => Redirect(routes.UserController.logout)
      }
    }.getOrElse {
      Redirect(routes.Application.index)
    }
  }

  /**
    * Edit a specific user.
    */
  def editUser(ofUser: Option[Long]) : Action[AnyContent] = Action { implicit request =>
    request.session.get("id").map { id =>
      val currentUser = UserService.getUserByID(id.toLong)
      currentUser match {
        case Some(currentUser) => ofUser match {
                                case Some(ofUser) => val user = UserService.getUserByID(ofUser)
                                                      user match {
                                                        case Some(user) => if(currentUser.admin) Ok(views.html.editUser(true, editUserForm, user)) else Redirect(routes.UserController.editUser(None))
                                                        case None => if(currentUser.admin) Redirect(routes.UserController.manageUser) else Redirect(routes.UserController.editUser(None))
                                                      }
                                case None => Ok(views.html.editUser(currentUser.admin, editUserForm, currentUser))
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
  def updateUser(id : Long) : Action[AnyContent] = Action { implicit request =>
      editUserForm.bindFromRequest.fold(
        formWithErrors => {
          Redirect(routes.UserController.editUser(Some(id))).flashing("fail" -> "Es ist ein Fehler unterlaufen!")
        },
        userData => {
          val user = models.User(userData.id, userData.name, userData.password, userData.admin, userData.distance)

          if(UserService.nameInUse(userData.id, userData.name)) {
            Redirect(routes.UserController.editUser(Some(user.id))).flashing("fail" -> "Username ist schon vergeben!")
          } else if(UserService.lastAdmin && !userData.admin) {
            Redirect(routes.UserController.editUser(Some(user.id))).flashing("fail" -> "Es muss mindestens einen Mitarbeiter geben!")
          } else {
            UserService.updateUser(user)
            Redirect(routes.UserController.editUser(Some(user.id))).flashing(
              "success" -> "The user has been updated!")
          }
        })
    }


  /**
    * Try to delete a specific User and go back to user overview.
    */
  def removeUser(id: Long) : Action[AnyContent] = Action { request =>
    request.session.get("id").map { userID =>
      val currentUser = UserService.getUserByID(userID.toLong)
      currentUser match {
        case Some(currentUser) => if(currentUser.admin) {
                                      if(UserService.userIsDeletable(id)) {
                                        if(UserService.userIsAdmin(id) == UserService.lastAdmin) {
                                          Redirect(routes.UserController.manageUser).flashing("fail" -> "Es muss mindestens einen Mitarbeiter geben!")
                                        } else {
                                          UserService.rmUser(id)
                                          Redirect(routes.UserController.manageUser).flashing("success" -> "User wurde erfolgreich gelöscht!")
                                        }
                                      } else {
                                        //UserService.deactivateUser(id)
                                        Redirect(routes.UserController.manageUser).flashing(
                                          "fail" -> "Der User wurde auf inaktiv gesetzt, da er nicht gelöscht werden konnte!")
                                      }
                                  } else Redirect(routes.Application.index)
        case None => Redirect(routes.Application.index)
      }
    }.getOrElse {
      Redirect(routes.Application.index)
    }
  }

  def logout() : Action[AnyContent] = Action {
    Redirect(routes.Application.index).withNewSession
  }

}
