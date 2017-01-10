package controllers

import play.api.mvc.{Action, AnyContent, Controller}
import play.api.data.Forms._ //{mapping,text,number}
import play.api.data.Form
import services._
import forms._

/**
  * Controller for order specific operations.
  *
  * @author ne
  */
object OrderController extends Controller {

  val orderForm = Form(
    mapping(
      "itemID" -> longNumber,
      "quantity" -> number,
      "size" -> number,
      "extraID" -> list(longNumber)
    )(CreateOrderForm.apply)(CreateOrderForm.unapply))

  val idForm = Form(
    mapping(
      "custID" -> longNumber)(CreateIDForm.apply)(CreateIDForm.unapply))

  def addOrder(username: String) : Action[AnyContent] = Action { implicit request =>
    orderForm.bindFromRequest.fold(
      formWithErrors => {
        val user = services.UserService.getUser(username).get
        BadRequest(views.html.welcomeUser(formWithErrors, user, 1))
      },
      userData => {
        val user = services.UserService.getUser(username).get
        //services.OrderService.addOrder(user.id, userData.itemID, userData.quantity, userData.size, 1, userData.extraID)
        services.OrderService.addOrder(user.id, userData.itemID, userData.quantity, userData.size, user.distance, userData.extraID)
        Redirect(routes.OrderController.showOrders(None))
      })
  }


  def refresh() : Action[AnyContent] = Action { implicit request =>
    idForm.bindFromRequest.fold(
      formWithErrors => {
        Redirect(routes.OrderController.showOrders(None)).
          flashing("fail" -> "Es ist ein Fehler aufgetreten!")
      },
      userData => {
        Redirect(routes.OrderController.showOrders(Some(userData.custID)))
      })
  }


  /**
    * List all orders of user in the system.
    */
  def showOrders(ofUser: Option[Long]) : Action[AnyContent] = Action { request =>
    request.session.get("id").map { id =>
      val user = services.UserService.getUserByID(id.toLong)
      user match {
        case Some(user) => if (user.admin) Ok(views.html.orders(true, ofUser.getOrElse(user.id)))
                          else if(user.id == ofUser.getOrElse(user.id)) Ok(views.html.orders(false, ofUser.getOrElse(user.id)))
                          else Redirect(routes.Application.index).flashing("error" -> "Ihnen fehlen Berechtigungen")
        case None => Redirect(routes.UserController.logout)
      }
    }.getOrElse {
      Redirect(routes.Application.index)
    }
  }


}
