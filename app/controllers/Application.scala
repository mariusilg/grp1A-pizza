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
  def index : Action[AnyContent] = Action { request =>
    request.session.get("id").map { id =>
      Redirect(routes.UserController.welcome(None))
    }.getOrElse {
      Ok(views.html.index(controllers.UserController.userForm, controllers.UserController.loginForm, UserService.registeredUsers))
    }
  }

}
