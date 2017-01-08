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
      Ok(views.html.privacy())
    }.getOrElse {
      Ok(views.html.privacy())
    }
  }

  /**
    * About page.
    *
    * @return about page
    */
  def about : Action[AnyContent] = Action { request =>
    request.session.get("id").map { id =>
      Ok(views.html.about())
    }.getOrElse {
      Ok(views.html.about())
    }
  }


  /**
    * Location page.
    *
    * @return location page
    */
  def location : Action[AnyContent] = Action { request =>
    request.session.get("id").map { id =>
      Ok(views.html.location())
    }.getOrElse {
      Ok(views.html.location())
    }
  }

}
