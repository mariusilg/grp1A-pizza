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
      "costs" -> number,
      "eQuantity" -> optional(number)
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
        userData.eQuantity match {
          case Some(eQuantity) => println(eQuantity)
          case None => println("nix übergeben")
        }
        services.OrderService.addOrder(user.id, userData.itemID, userData.quantity, userData.size, userData.costs * userData.size * userData.quantity)
        Redirect(routes.OrderController.showOrders(username, user.id)).
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
        Redirect(routes.OrderController.showOrders(username, userData.custID)).
          flashing("success" -> "User saved!")
      })
  }


  /**
    * List all orders of user in the system.
    */
  def showOrders(username: String, ofUser: Long) : Action[AnyContent] = Action {
    val user = services.UserService.getUser(username).get
    if (user.admin) Ok(views.html.orders(true, ofUser){user})
    else if(user.id == ofUser) Ok(views.html.orders(false, ofUser){user})
    else Forbidden("Kein Zugriff auf diese Bestellungen")
  }


}
