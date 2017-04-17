package controllers
import models.User
import play.api.data.Form
import play.api.data.Forms.{text, tuple}
import play.api.mvc._
import services.UserService


/**
  * Created by philipp on 17.12.16.
  */
object Auth extends Controller {

  val loginForm = Form(
    tuple(
      "UserName" -> text,
      "Password" -> text
    ) verifying ("Invalid user or password", result => result match {
      case (user, password) => check(user, password)
    })
  )

  def check(username: String, password: String) = {
    println("test3")
    println("username " + username + " password: " + password)
    if(UserService.checkIfUserExists(username, password)) {
      var user = UserService.getUser(username)
      UserService.userIsActive(user.get.id)
    } else {
      false
    }


    //(username == "admin" && password == "1234")
  }


  def login : Action[AnyContent] = Action {
    //Ok(views.html.login(UserService.registeredUsers))
    Ok(views.html.index(controllers.UserController.loginForm))
  }



  def post_authenticate = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => {
        Redirect(routes.Auth.login)
      },
      user => {
        Redirect(routes.Application.index).withSession(Security.username -> user._1)

      }
    )
  }


  def logout = Action {
    Redirect(routes.Auth.login).withNewSession.flashing(
      "success" -> "You are now logged out."
    )
  }






  trait Secured {

    def username(request: RequestHeader) = {
      println("username method")
      println(request.session.get(Security.username))
      request.session.get(Security.username)
    }



    def onUnauthorized(request: RequestHeader) = {
      println("onUnauthorized")
      Results.Redirect(routes.Auth.login)
    }

    def notAuthorized(request: RequestHeader) = Results.Redirect(routes.Application.index)



    def withAuth(f: => String => Request[AnyContent] => Result) = {
      println("withAuth")
      Security.Authenticated(username, onUnauthorized) { user =>
        Action(request => f(user)(request))
      }
    }

    def getUsername(f: => String => Request[AnyContent] => Result) = {
      Security.username
    }







    /**
      * This method shows how you could wrap the withAuth method to also fetch your user
      * You will need to implement UserDAO.findOneByUsername
      */
    /*def withUser(f: AppUser => Request[AnyContent] => Result) = withAuth {
      username => implicit request =>
      UserDao.findOneByUsername(username).map {
        user => f(user)(request)
      }.getOrElse(onUnauthorized(request))
    }*/

    def withUser(f: User => Request[AnyContent] => Result) = withAuth {
      username => implicit request =>
        var user = UserService.getUser(username)
        if(user.nonEmpty) {
          f(user.get)(request)
        } else {
          onUnauthorized(request)
        }
    }



    def withUser_Employee(f: User => Request[AnyContent] => Result) = withAuth {
      username => implicit request =>
        var user = UserService.getUser(username)
        if(UserService.userIsAdmin(user.get.id)) {
          f(user.get)(request)
        } else {
          notAuthorized(request)
        }
    }

    def withUser_Customer(f: User => Request[AnyContent] => Result) = withAuth {
      username => implicit request =>
        println("withUser_customer" + username)
        var user = UserService.getUser(username)
        if(!UserService.userIsAdmin(user.get.id)) {
          f(user.get)(request)
        } else {
          notAuthorized(request)
        }
    }

  }
}
