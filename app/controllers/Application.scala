package controllers

import play.api.mvc.{Action, Controller, AnyContent}
import services.UserService

/**
 * Main controller of the Pizza Service application.
 *
 * @author ob, scs
 */
object Application extends Controller {

  /**
   * Shows the start page of the application.
   *
   * @return main web page
   */
  def index : Action[AnyContent] = Action {
    Ok(views.html.index(controllers.UserController.userForm, controllers.UserController.loginForm, UserService.registeredUsers))
  }

}
