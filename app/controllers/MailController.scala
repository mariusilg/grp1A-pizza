package controllers
import controllers.Auth._
import play.api.mvc.{Action, AnyContent, Controller}
import mail._
import org.apache.commons.mail.EmailException

object MailController extends Controller with Secured {

  def sendMail(user: models.User, order: models.Order)= {
    try {
      val sendMail = send a new Mail(
        from = (models.Company.email, models.Company.name),
        to = "nils.engelbrecht@gmx.net",
        subject = "Bestellbestaetigung",
        message = " ",
        richMessage = Option("Vielen Dank fuer <b>Ihre</b> Bestellung!\nDie Bestellung ist am " + order.date.toString.substring(0, 16) + " bei uns eingegangen\n"
          + "Gesamtbetrag: " + order.costsToString)
      )
  } catch {
      case e: EmailException              => println("eMail Exception");
    }
  }

  def confirmMail(user: models.User, token: String) = {
    try {
      val sendMail = send a new Mail(
        from = (models.Company.email, models.Company.name),
        to = user.email,
        subject = "Bestaetigen Sie ihren Account bei " + models.Company.name,
        message = " ",
        richMessage = Option("Vielen Dank " + user.firstName + " für deine <b>Registrierung</b> bei " + models.Company.name + "!<br/>"
          + "Bitte bestätige die Registierung indem du auf nachfolgenden Link klickst: <br/><br/>"
          + "<a href='" + models.Company.link + "/confirm?id=" + user.id + "&token=" + token + "'>" + models.Company.link + "/confirm?id=" + user.id + "&token=" + token + "</a><br/><br/>"
          + "Vielen Dank<br/><br/>"
          + "- dein Team von " + models.Company.name)
      )
    } catch {
      case e: EmailException              => println("eMail Exception");
    }
  }

  /**
    * Shows the welcome view for a (newly) registered user.
    */
  def resendConfirm(id: Long) = withUser_Employee { user => request =>
      val user = services.UserService.getUserByID(id)
      user match{
        case Some(user) =>
          if(!user.active) {
            services.UserService.getTokenByUserID(user.id).map{token => confirmMail(user, token)}
            Redirect(routes.UserController.editUser(Some(id))).flashing("success" -> "Email wurde erneut versandt")
          } else {
            Redirect(routes.UserController.editUser(Some(id))).flashing("fail" -> "User ist schon aktiv")
          }
        case _ => Redirect(routes.UserController.editUser(None))
      }
    }

}
