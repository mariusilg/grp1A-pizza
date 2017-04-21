package controllers

import controllers.ItemController._
import forms._
import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc.{Action, AnyContent, Controller}

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

  def addOrder(username: String) = withUser { user => implicit request =>
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

  def addToCart = withUser { user => implicit request =>
          orderForm.bindFromRequest.fold(
          formWithErrors => {
            BadRequest(views.html.welcomeUser(formWithErrors, user, 1))
          },
          userData => {
              services.OrderService.addToCart(user.id, userData.itemID, userData.quantity, userData.size, user.distance, userData.extraID)
              Redirect(routes.OrderController.showCart())
          })
  }

  def confirmCart = withUser { user => implicit request =>
            if(user.distance <= 20) {
              if (services.OrderService.confirmCart(user.id)) {
                controllers.WSController.sendNotification(user)
                Redirect(routes.OrderController.showOrders(None))
              } else {
                Redirect(routes.OrderController.showCart)
              }
            } else {
              Redirect(routes.UserController.editUser(None)).flashing("fail" -> "Bestellung konnte nicht aufgenommen werden, da wir nicht weiter als 20 Kilometer ausliefern")
            }
  }





  def refresh() = withUser { user => implicit request =>
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
  def showOrders(ofUser: Option[Long]) = withUser { user => implicit request =>
      user.admin match {
        case true =>
          Ok(views.html.orders(true, ofUser.getOrElse(user.id)))

        case false =>
          if (user.id == ofUser.getOrElse(user.id)) {
            Ok(views.html.orders(false, ofUser.getOrElse(user.id)))
          } else {
            Redirect(routes.Application.index).flashing("error" -> "Ihnen fehlen Berechtigungen")
          }
      }
  }

  /**
    * List cart of a specific user.
    */
  def showCart = withUser_Customer { user => implicit request =>
     Ok(views.html.cart(user, services.OrderService.getCartByUserID(user.id)))
  }


  def deleteCart = withUser_Customer { user => implicit request =>
          if (services.OrderService.deleteCart(user.id)) {
            Redirect(routes.OrderController.showCart)
          } else {
            Redirect(routes.OrderController.showCart).flashing("fail" -> "Warenkorb konnte nicht gelöscht werden")
          }
  }

  def deleteCartItem(orderItemID: Long) = withUser_Customer { user => implicit request =>
          if (services.OrderService.deleteCartItem(user.id, orderItemID)) {
            Redirect(routes.OrderController.showCart).flashing("success" -> "Produkt wurde aus dem Warenkorb gelöscht")
          } else {
            Redirect(routes.OrderController.showCart).flashing("fail" -> "Produkt konnte nicht aus dem Warenkorb gelöscht werden")
          }
  }

  def deleteCartExtra(orderExtraID: Long) = withUser_Customer { user => implicit request =>
    if (services.OrderService.deleteCartExtra(user.id, orderExtraID)) {
      Redirect(routes.OrderController.showCart).flashing("success" -> "Extra wurde aus dem Warenkorb gelöscht")
    } else {
      Redirect(routes.OrderController.showCart).flashing("fail" -> "Extra konnte nicht aus dem Warenkorb gelöscht werden")
    }
  }

  def cancelOrder(orderID: Long)= withUser_Customer { user => implicit request =>
          if (services.OrderService.cancelOrder(user.id, orderID)) {
            Redirect(routes.OrderController.showOrders(None))
          } else {
            Redirect(routes.OrderController.showOrders(None)).flashing("fail" -> "Bestellung konnte nicht storniert werden")
          }
  }



}
