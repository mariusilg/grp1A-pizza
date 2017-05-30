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
        routes.javascript.JavascriptRoutes.checkUser,
        routes.javascript.JavascriptRoutes.checkIfUserExists,
        routes.javascript.AssortmentController.rmExtra
      )
    ).as("text/javascript")
  }

  def checkUser(username: String) = Action {
    //Ok(views.html.alertUserExists("hallo"))
    println(username)
    Ok("true")
  }

  def checkIfUserExists(username: String) = Action {
    //Ok(views.html.alertUserExists("hallo"))
    var returnValue = services.UserService.nameInUse(username)
    Ok(returnValue.toString)
  }

  def jsroutetest : Action[AnyContent] = Action {
    Ok(views.html.jsroutetest())
  }

}
