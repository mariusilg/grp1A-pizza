package controllers

import controllers.Auth.Secured
import forms._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, AnyContent, Controller, Security}
import services._

/**
 * Controller for user specific operations.
 *
 * @author ob, scs, ne
 */
object UserController extends Controller with Secured {

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
    )(LoginForm.apply)(LoginForm.unapply))

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
  def addUser = withUser_Employee { user => implicit request =>
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



  /**
    * Shows the welcome view for a (newly) registered user.
    */

  /*def index = withUser { user => implicit request =>
    user.admin match {
      case true =>
        Redirect(routes.UserController.showUsers())
      //Ok(views.html.index(controllers.UserController.userForm))
      case _ =>
        //Ok(views.html.index(controllers.UserController.userForm))
        Redirect(routes.UserController.showOwnUser())

    }
  }*/
  def home = withUser { user => implicit request =>
    Ok(views.html.home())
  }

  def welcome(categoryID: Option[Long]) = withUser { user => request =>
        if (user.admin) {
          Ok(views.html.welcomeAdmin(user))
        } else {
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
  }

  /**
    * Shows the welcome view for a (newly) registered user.
    */
  def confirm(id: Long, token: String) : Action[AnyContent] = Action { request =>
          if(services.UserService.confirmAccount(id, token)) {
            var user = services.UserService.getUserByID(id)
              user match {
                case Some(user) => Ok(views.html.confirmAccount()).withSession(Security.username -> user.userName)
                case _ => Redirect(routes.UserController.welcome(None))
              }
            } else {
            Redirect(routes.UserController.welcome(None))
            }
  }

  /**
    * Manage all users which are currently available in the system.
    */
  def manageUser() = withUser_Employee { user => implicit request =>
       Ok(views.html.users(userForm, UserService.registeredUsers))
  }


  /**
    * Edit a specific user.
    */
  def editUser(userToEdit: Option[Long]) = withUser { user => implicit request =>
        userToEdit match {
             case Some(userToEdit) =>
               val editedUser = UserService.getUserByID(userToEdit)

               editedUser match {
                 case Some(userToEdit) =>
                   if(user.admin) {
                     Ok(views.html.editUser(true, editUserForm, userToEdit))
                   } else {
                     Ok(views.html.editUser(user.admin, editUserForm, user))
                   }

                 case None =>
                   if(user.admin) {
                     Redirect(routes.UserController.manageUser)
                   } else {
                     Ok(views.html.editUser(user.admin, editUserForm, user))
                   }
               }

             case None =>
               Ok(views.html.editUser(user.admin, editUserForm, user))
        }


  }

  /**
    * Update a specific user and go back to user overview.
    */
  def updateUser(id : Long) = withUser { user => implicit request =>
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
  def removeUser(id: Long) = withUser_Employee { currentUser =>  request =>
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
  }

}
