package controllers

import controllers.Auth.Secured
import play.api.Routes
import play.api.mvc.{Action, AnyContent, Controller}

/**
  * Created by philipp on 09.04.17.
  */
object JavascriptRoutes extends Controller with Secured {

  def javascriptRoutes = Action { implicit request =>
    Ok(
      Routes.javascriptRouter("jsRoutes")(
        routes.javascript.JavascriptRoutes.checkName,
        routes.javascript.JavascriptRoutes.checkOtherName,
        routes.javascript.JavascriptRoutes.checkEmail,
        routes.javascript.JavascriptRoutes.checkOtherEmail
      )
    ).as("text/javascript")
  }

  def checkName(username: String) = Action {
    var returnValue = services.UserService.nameInUse(username.trim)
    Ok(returnValue.toString)
  }

  def checkOtherName(id: Long, username: String) = Action {
    var returnValue = services.UserService.nameInUse(id, username.trim)
    Ok(returnValue.toString)
  }

  def checkEmail(email: String) = Action {
    var returnValue = services.UserService.eMailInUse(email.trim)
    Ok(returnValue.toString)
  }

  def checkOtherEmail(id: Long, email: String) = Action {
    var returnValue = services.UserService.eMailInUse(id, email.trim)
    Ok(returnValue.toString)
  }

  def jsroutetest : Action[AnyContent] = Action {
    Ok(views.html.jsroutetest())
  }

}
