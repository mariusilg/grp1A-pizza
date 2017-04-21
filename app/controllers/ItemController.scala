package controllers

import controllers.Auth.Secured
import forms.CreateOrderForm
import play.api.data.Form
import play.api.data.Forms.{list, longNumber, mapping, number}
import play.api.mvc.Controller

/**
  * Created by philipp on 15.04.17.
  */
object ItemController extends Controller with Secured {
  val orderForm = Form(
    mapping(
      "itemID" -> longNumber,
      "quantity" -> number,
      "size" -> number,
      "extraID" -> list(longNumber)
    )(CreateOrderForm.apply)(CreateOrderForm.unapply))

  def showItems(categoryID: Long) = withUser_Customer { user => implicit request =>
    Ok(views.html.welcomeUser(controllers.ItemController.orderForm, user, categoryID))
  }
}
