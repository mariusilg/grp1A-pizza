package controllers

import play.api.mvc.{Action, AnyContent, Controller}
import play.api.data.Forms._
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
        if(user.distance <= 20) {
          services.OrderService.addOrder(user.id, userData.itemID, userData.quantity, userData.size, user.distance, userData.extraID)
          Redirect(routes.OrderController.showOrders(None))
        }
        else {
          Redirect(routes.UserController.editUser(None)).flashing("fail" -> "Bestellung konnte nicht aufgenommen werden, da wir nicht weiter als 20 Kilometer ausliefern")
        }

      })
  }

  def addToCart: Action[AnyContent] = Action { implicit request =>
    request.session.get("id").map { id =>
      val user = services.UserService.getUserByID(id.toLong)
      user match {
        case Some(user) => orderForm.bindFromRequest.fold(
          formWithErrors => {
            BadRequest(views.html.welcomeUser(formWithErrors, user, 1))
          },
          userData => {
              services.OrderService.addToCart(user.id, userData.itemID, userData.quantity, userData.size, user.distance, userData.extraID)
              Redirect(routes.OrderController.showCart())
          })
        case None => Redirect(routes.UserController.logout)
      }
    }.getOrElse {
      Redirect(routes.Application.index)
    }
  }

  def confirmCart: Action[AnyContent] = Action { implicit request =>
    request.session.get("id").map { id =>
      val user = services.UserService.getUserByID(id.toLong)
      user match {
        case Some(user) =>
            if(user.distance <= 20) {
              if (services.OrderService.confirmCart(user.id)) {
                Redirect(routes.OrderController.showOrders(None))
              } else {
                Redirect(routes.OrderController.showCart)
              }
            } else {
              Redirect(routes.UserController.editUser(None)).flashing("fail" -> "Bestellung konnte nicht aufgenommen werden, da wir nicht weiter als 20 Kilometer ausliefern")
            }
        case None => Redirect(routes.UserController.logout)
      }
    }.getOrElse {
      Redirect(routes.Application.index)
    }
  }

  def deleteCart: Action[AnyContent] = Action { implicit request =>
    request.session.get("id").map { id =>
      val user = services.UserService.getUserByID(id.toLong)
      user match {
        case Some(user) =>
            if (services.OrderService.deleteCart(user.id)) {
              Redirect(routes.OrderController.showCart)
            } else {
              Redirect(routes.OrderController.showCart).flashing("fail" -> "Warenkorb konnte nicht gelöscht werden")
            }
        case None => Redirect(routes.UserController.logout)
      }
    }.getOrElse {
      Redirect(routes.Application.index)
    }
  }

  def deleteCartItem(orderItemID: Long): Action[AnyContent] = Action { implicit request =>
    request.session.get("id").map { id =>
      val user = services.UserService.getUserByID(id.toLong)
      user match {
        case Some(user) =>
          if (services.OrderService.deleteCartItem(user.id, orderItemID)) {
            Redirect(routes.OrderController.showCart)
          } else {
            Redirect(routes.OrderController.showCart).flashing("fail" -> "Produkt konnte nicht aus dem Warenkorb gelöscht werden")
          }
        case None => Redirect(routes.UserController.logout)
      }
    }.getOrElse {
      Redirect(routes.Application.index)
    }
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
    * List orders of a specific user or of all users in the system.
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

  /**
    * List cart of a specific user.
    */
  def showCart : Action[AnyContent] = Action { request =>
    request.session.get("id").map { id =>
      val user = services.UserService.getUserByID(id.toLong)
      user match {
        case Some(user) => Ok(views.html.cart(user, services.OrderService.getCartByUserID(user.id)))
        case None => Redirect(routes.UserController.logout)
      }
    }.getOrElse {
      Redirect(routes.Application.index)
    }
  }

}
