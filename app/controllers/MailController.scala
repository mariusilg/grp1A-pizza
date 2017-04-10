package controllers
import play.api.mvc.{Action, AnyContent, Controller}
import mail._
import org.apache.commons.mail.EmailException

object MailController extends Controller {

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

}
