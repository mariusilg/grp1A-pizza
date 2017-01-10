package controllers

import play.api.mvc.{Action, Controller, AnyContent}
import services.UserService

/**
 * Main controller of the Pizza Service application.
 *
 * @author ob, scs, ne
 */
object Application extends Controller {

  /**
   * Shows the start page of the application.
   *
   * @return main web page
   */
  def index : Action[AnyContent] = Action { request =>
    request.session.get("id").map { id =>
      val user = UserService.getUserByID(id.toLong)
      user match {
        case Some(user) => Redirect(routes.UserController.welcome(None))
        case None => Redirect(routes.UserController.logout)
      }
    }.getOrElse {
      Ok(views.html.index(controllers.UserController.loginForm))
    }
  }

  /**
    * Shows the Registration page.
    *
    * @return registration page for unknown user
    */
  def register : Action[AnyContent] = Action { request =>
    request.session.get("id").map { id =>
      Redirect(routes.UserController.welcome(None))
    }.getOrElse {
      Ok(views.html.registration(controllers.UserController.userForm))
    }
  }

  /**
    * Shows the privacy page.
    *
    * @return privacy page
    */
  def privacy : Action[AnyContent] = Action { request =>
    request.session.get("id").map { id =>
      val user = UserService.getUserByID(id.toLong)
      user match {
        case Some(user) => Ok(views.html.privacy(true, user.admin))
        case None => Redirect(routes.UserController.logout)
      }
    }.getOrElse {
      Ok(views.html.privacy(false, false))
    }
  }

  /**
    * Shows the about page.
    *
    * @return about page
    */
  def about : Action[AnyContent] = Action { request =>
    request.session.get("id").map { id =>
      val user = UserService.getUserByID(id.toLong)
      user match {
        case Some(user) => Ok(views.html.about(true, user.admin))
        case None => Redirect(routes.UserController.logout)
      }
    }.getOrElse {
      Ok(views.html.about(false, false))
    }
  }


  /**
    * Shows the location page.
    *
    * @return location page
    */
  def location : Action[AnyContent] = Action { request =>
    request.session.get("id").map { id =>
      val user = UserService.getUserByID(id.toLong)
      user match {
        case Some(user) => Ok(views.html.location(true, user.admin))
        case None => Redirect(routes.UserController.logout)
      }
    }.getOrElse {
      Ok(views.html.location(false, false))
    }
  }

  /**
    * Shows the error page.
    *
    * @return error page
    */
  def error : Action[AnyContent] = Action { implicit request =>
    request.session.get("id").map { id =>
      val user = UserService.getUserByID(id.toLong)
      user match {
        case Some(user) => Ok(views.html.error(true, user.admin))
        case None => Redirect(routes.UserController.logout)
      }
    }.getOrElse {
      Ok(views.html.error(false, false))
    }
  }

}
