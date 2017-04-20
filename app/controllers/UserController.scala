package controllers

import play.api.mvc.{Action, AnyContent, Controller}
import play.api.data.Form
import play.api.data.Forms._
import services._
import forms._

/**
 * Controller for user specific operations.
 *
 * @author ob, scs, ne
 */
object UserController extends Controller {

  /**
   * Form object for user data.
   */
  val userForm = Form(
    mapping(
      "UserName" -> text.verifying("Bitte Username angeben!", f => f.trim!="").verifying("Bitte geben Sie einen validen Usernamen an", name => name.matches("[A-z\\s]+")).verifying("Username existiert bereits", name => !services.UserService.nameInUse(name)),
      "FirstName" -> text,
      "LastName" -> text,
      "Password" -> text.verifying("Passwort fehlt!", f => f.trim!=""),
      "Admin" -> optional(boolean),
      "Street" -> text,
      "Zip" -> text,
      "City" -> text,
      "Phone" -> text,
      "Email" -> text,
      "Active" -> optional(boolean)
    )(CreateUserForm.apply)(CreateUserForm.unapply))

  /**
    * Form object for editing user data.
    */
  val editUserForm = Form(
    mapping(
      "ID" -> longNumber,
      "UserName" -> text.verifying("Bitte geben Sie einen Username an!", f => f.trim!="").verifying("Bitte gebe Sie einen validen Usernamen an", name => name.matches("[A-z\\s]+")),
      "FirstName" -> text,
      "LastName" -> text,
      "Password" -> text.verifying("Passwort fehlt", f => f.trim!=""),
      "Admin" -> boolean,
      "Street" -> text,
      "Zip" -> text,
      "City" -> text,
      "Phone" -> text,
      "Email" -> text,
      "Active" -> boolean
    )(EditUserForm.apply)(EditUserForm.unapply)
    /*verifying("Username existiert bereits", fields => fields match {
      case userData => userData.id == userData.id
    })*/
  )

  /**
    * Form object for login data.
    */
  val loginForm = Form(
    mapping(
      "UserName" -> text.verifying("Bitte Username angeben!", f => f.trim!="").verifying("Bitte gebe einen validen Usernamen an", name => name.matches("[A-z\\s]+")).verifying("Username existiert nicht", name => services.UserService.nameInUse(name)),
      "Password" -> text.verifying("Passwort fehlt", f => f.trim!="")
    )(CreateLoginForm.apply)(CreateLoginForm.unapply))


  /**
   * Registers a new user with the given data and adds him to the system.
   *
   * @return first available category page
   */
  def register : Action[AnyContent] = Action { implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.registration(formWithErrors))
      },
      userData => {
        val token = controllers.Auth.generateMD5Token(userData.userName)
        val newUser = services.UserService.addUser(userData.userName, userData.firstName, userData.lastName, userData.password, false, userData.street, userData.zip, userData.city, userData.phone, userData.email, false, token)
          Redirect(routes.UserController.welcome(None))
      })
  }

  /**
    * Adds a new user with the given data to the system.
    *
    * @return first available category page
    */
  def addUser : Action[AnyContent] = Action { implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.users(formWithErrors, UserService.registeredUsers))
      },
      userData => {
        val token = controllers.Auth.generateMD5Token(userData.userName)
        val newUser = services.UserService.addUser(userData.userName, userData.firstName, userData.lastName, userData.password, userData.admin.getOrElse(false),
          userData.street, userData.zip, userData.city, userData.phone, userData.email, userData.active.getOrElse(false), token)
        Redirect(routes.UserController.manageUser).flashing("success" -> "User wurde erfolgreich angelegt")
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
          case Some(id) => if (UserService.userIsActive(id)) {
            Redirect(routes.UserController.welcome(None)) withSession("id" -> id.toString)
          } else {
            Redirect(routes.Application.error()).flashing("error" -> "Ihr Account ist deaktiviert worden - Bitte kontaktieren Sie uns")
          }
          case None => Redirect(routes.Application.register)
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
        case Some(user) => if (user.admin) {Ok(views.html.welcomeAdmin(user))}
                           else {
                              categoryID match {
                                case Some(categoryID) => if(services.CategoryService.isCategoryVisible(categoryID)) {
                                                          Ok(views.html.welcomeUser(controllers.OrderController.orderForm, user, categoryID))
                                                        } else {
                                                          Redirect(routes.UserController.welcome(None))
                                                        }
                                case None => val defaultCategory = services.CategoryService.getDefaultCategory
                                              defaultCategory match {
                                                case Some(defaultCategory) => Ok(views.html.welcomeUser(controllers.OrderController.orderForm, user, defaultCategory))
                                                case None => Forbidden("Leider liegt ein Fehler vor!")
                                              }
                              }
                           }
        case None => Redirect(routes.UserController.logout)
      }
    }.getOrElse {
      Redirect(routes.Application.index)
    }
  }

  /**
    * Shows the welcome view for a (newly) registered user.
    */
  def confirm(id: Long, token: String) : Action[AnyContent] = Action { request =>
          if(services.UserService.confirmAccount(id, token)) {
            Ok(views.html.confirmAccount()) withSession("id" -> id.toString)
            } else {
              Redirect(routes.UserController.welcome(None))
            }
  }

  /**
    * Manage all users which are currently available in the system.
    */
  def manageUser() : Action[AnyContent] = Action { implicit request =>
    request.session.get("id").map { id =>
      val user = services.UserService.getUserByID(id.toLong)
      user match {
        case Some(user) => if(user.admin) Ok(views.html.users(userForm, UserService.registeredUsers)) else Redirect(routes.Application.error).flashing("error" -> "kein Zutritt")
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
                                                        case Some(user) => if(currentUser.admin) Ok(views.html.editUser(true, editUserForm, user)) else Ok(views.html.editUser(currentUser.admin, editUserForm, currentUser))
                                                        case None => if(currentUser.admin) Redirect(routes.UserController.manageUser) else Ok(views.html.editUser(currentUser.admin, editUserForm, currentUser))
                                                      }
                                case None => Ok(views.html.editUser(currentUser.admin, editUserForm, currentUser))
                                }
        case None => Redirect(routes.UserController.logout)
      }
    }.getOrElse {
      Redirect(routes.Application.index)
    }
  }

  /**
    * Update a specific user and go back to user overview.
    */
  def updateUser(id : Long) : Action[AnyContent] = Action { implicit request =>
      editUserForm.bindFromRequest.fold(
        formWithErrors => {
          Redirect(routes.UserController.editUser(Some(id))).flashing("fail" -> "Es ist ein Fehler unterlaufen!")
        },
        userData => {
          val user = models.User(userData.id, userData.userName, userData.firstName, userData.lastName, userData.password, userData.admin, userData.street, userData.zip, userData.city, userData.phone, userData.email, -1, userData.active)
          if(UserService.nameInUse(userData.id, userData.userName)) {
            Redirect(routes.UserController.editUser(Some(user.id))).flashing("fail" -> "Username ist schon vergeben!")
          } else if((UserService.lastAdmin(user.id) && !user.admin) || (UserService.lastAdmin(user.id) && !user.active)) {
            Redirect(routes.UserController.editUser(Some(user.id))).flashing("fail" -> "Es muss mindestens einen (aktiven) Mitarbeiter geben!")
          } else {
            UserService.updateUser(user)
            Redirect(routes.UserController.editUser(Some(user.id))).flashing(
              "success" -> "Der User wurde erfolgreich aktualisiert")
          }
        })
    }


  /**
    * Try to delete a specific user and go back to user overview.
    */
  def removeUser(id: Long) : Action[AnyContent] = Action { request =>
    request.session.get("id").map { userID =>
      val currentUser = UserService.getUserByID(userID.toLong)
      currentUser match {
        case Some(currentUser) => if(currentUser.admin) {
                                      if(UserService.userIsDeletable(id)) {
                                        if(UserService.lastAdmin(id)) {
                                          Redirect(routes.UserController.manageUser).flashing("fail" -> "Es muss mindestens einen Mitarbeiter geben!")
                                        } else {
                                          UserService.rmUser(id)
                                          Redirect(routes.UserController.manageUser).flashing("success" -> "User wurde erfolgreich gelöscht!")
                                        }
                                      } else {
                                        val success = UserService.deactivateUser(id)
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

  /**
    * Loos user out and goes back to the index page.
    */
  def logout() : Action[AnyContent] = Action {
    Redirect(routes.Application.index).withNewSession
  }

}
