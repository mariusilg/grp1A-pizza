package controllers
import models.User
import play.api.data.Form
import play.api.data.Forms.{text, tuple}
import play.api.mvc._
import services.UserService

import scala.util._
import java.security.SecureRandom
import java.security.MessageDigest


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
    val user = UserService.login(username, password)
    user match {
      case Some(user) => user.active
      case _ => false
    }
  }

  def login : Action[AnyContent] = Action {
    Ok(views.html.index(loginForm))
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

  //https://gist.github.com/jeffsteinmetz/063bd3237033f3af2ed9
  val TOKEN_LENGTH = 45	// TOKEN_LENGTH is not the return size from a hash,
  // but the total characters used as random token prior to hash
  // 45 was selected because System.nanoTime().toString returns
  // 19 characters.  45 + 19 = 64.  Therefore we are guaranteed
  // at least 64 characters (bytes) to use in hash, to avoid MD5 collision < 64
  val TOKEN_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz_.-"
  val secureRandom = new SecureRandom()

  private def toHex(bytes: Array[Byte]): String = bytes.map( "%02x".format(_) ).mkString("")

  private def sha(s: String): String = {
    toHex(MessageDigest.getInstance("SHA-256").digest(s.getBytes("UTF-8")))
  }
  private def md5(s: String): String = {
    toHex(MessageDigest.getInstance("MD5").digest(s.getBytes("UTF-8")))
  }

  // use tail recursion, functional style to build string.
  private def generateToken(tokenLength: Int) : String = {
    val charLen = TOKEN_CHARS.length()
    def generateTokenAccumulator(accumulator: String, number: Int) : String = {
      if (number == 0) return accumulator
      else
        generateTokenAccumulator(accumulator + TOKEN_CHARS(secureRandom.nextInt(charLen)).toString, number - 1)
    }
    generateTokenAccumulator("", tokenLength)
  }

  /*
   *  Hash the Token to return a 32 or 64 character HEX String
   *
   *  Parameters:
   *  tokenprifix: string to concatenate with random generated token prior to HASH to improve uniqueness, such as username
     *
     *  Returns:
     *  MD5 hash of (username + current time + random token generator) as token, 128 bits, 32 characters
     * or
     *  SHA-256 hash of (username + current time + random token generator) as token, 256 bits, 64 characters
     */
  def generateMD5Token(tokenprefix: String): String =  {
    md5(tokenprefix + System.nanoTime() + generateToken(TOKEN_LENGTH))
  }

  def generateSHAToken(tokenprefix: String): String =  {
    sha(tokenprefix + System.nanoTime() + generateToken(TOKEN_LENGTH))
  }


  trait Secured {

    def username(request: RequestHeader) = {
      request.session.get(Security.username)
    }

    def onUnauthorized(request: RequestHeader) = {
      Results.Redirect(routes.Auth.login)
    }

    def notAuthorized(request: RequestHeader) = Results.Redirect(routes.Application.index)


    def withAuth(f: => String => Request[AnyContent] => Result) = {
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
        var user = UserService.getUser(username)
        if(!UserService.userIsAdmin(user.get.id)) {
          f(user.get)(request)
        } else {
          notAuthorized(request)
        }
    }

  }
}
