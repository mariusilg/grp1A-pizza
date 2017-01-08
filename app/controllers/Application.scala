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
      val user = services.UserService.getUserByID(id.toLong)
      user match {
        case Some(user) => Redirect(routes.UserController.welcome(None))
        case None => Redirect(routes.UserController.logout)
      }
    }.getOrElse {
      Ok(views.html.index(controllers.UserController.loginForm))
    }
  }

  /**
    * Registration page.
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
    * privacy page.
    *
    * @return privacy page
    */
  def privacy : Action[AnyContent] = Action { request =>
    request.session.get("id").map { id =>
      val user = services.UserService.getUserByID(id.toLong)
      user match {
        case Some(user) => Ok(views.html.privacy(true, user.admin))
        case None => Redirect(routes.UserController.logout)
      }
    }.getOrElse {
      Ok(views.html.privacy(false, false))
    }
  }

  /**
    * About page.
    *
    * @return about page
    */
  def about : Action[AnyContent] = Action { request =>
    request.session.get("id").map { id =>
      val user = services.UserService.getUserByID(id.toLong)
      user match {
        case Some(user) => Ok(views.html.about(true, user.admin))
        case None => Redirect(routes.UserController.logout)
      }
    }.getOrElse {
      Ok(views.html.about(false, false))
    }
  }


  /**
    * Location page.
    *
    * @return location page
    */
  def location : Action[AnyContent] = Action { request =>
    request.session.get("id").map { id =>
      val user = services.UserService.getUserByID(id.toLong)
      user match {
        case Some(user) => Ok(views.html.location(true, user.admin))
        case None => Redirect(routes.UserController.logout)
      }
    }.getOrElse {
      Ok(views.html.location(false, false))
    }
  }

}
