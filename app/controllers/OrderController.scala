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
        Redirect(routes.OrderController.showOrders(None)).
          flashing("success" -> "User saved!")
      })
  }


  def refresh(username: String) : Action[AnyContent] = Action { implicit request =>
    idForm.bindFromRequest.fold(
      formWithErrors => {
        val user = services.UserService.getUser(username).get
        BadRequest(views.html.orders(user.admin, 0){user})
      },
      userData => {
        val user = services.UserService.getUser(username).get
        Redirect(routes.OrderController.showOrders(Some(userData.custID))).
          flashing("success" -> "User saved!")
      })
  }


  /**
    * List all orders of user in the system.
    */
  def showOrders(ofUser: Option[Long]) : Action[AnyContent] = Action { request =>
    request.session.get("id").map { id =>
      val user = services.UserService.getUserByID(id.toLong).get
      if (user.admin) Ok(views.html.orders(true, ofUser.getOrElse(user.id)){user})
      else if(user.id == ofUser.getOrElse(user.id)) Ok(views.html.orders(false, ofUser.getOrElse(user.id)){user})
      else Forbidden("Kein Zugriff auf diese Bestellungen")
    }.getOrElse {
      Redirect(routes.Application.index)
    }

  }


}
