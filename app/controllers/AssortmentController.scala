package controllers

import play.api.mvc.{Action, AnyContent, Controller}
import play.api.data.Forms._ //{mapping,text,number}
import play.api.data.Form
import services._
import forms._

/**
  * Controller for assortment specific operations.
  *
  * @author ne
  */
object AssortmentController extends Controller {

  val itemForm = Form(
    mapping(
      "CategoryID" -> longNumber,
      "Name" -> text,
      "Price" -> number,
      "Visibility" -> optional(boolean)
    )(CreateItemForm.apply)(CreateItemForm.unapply))

  val editItemForm = Form(
    mapping(
      "ID" -> longNumber,
      "CategoryID" -> longNumber,
      "Name" -> text,
      "Price" -> number,
      "Visibility" -> optional(boolean)
    )(EditItemForm.apply)(EditItemForm.unapply))

  val categoryForm = Form(
    mapping(
      "ID" -> optional(longNumber),
      "Name" -> text,
      "Visibility" -> optional(boolean)
    )(CreateCategoryForm.apply)(CreateCategoryForm.unapply))


  def addCategory : Action[AnyContent] = Action { implicit request =>
    categoryForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.assortment(formWithErrors, itemForm))
      },
      categoryData => {
        services.CategoryService.addCategory(categoryData.name, categoryData.visibility.getOrElse(false))
        Redirect(routes.AssortmentController.manageAssortment)
      })
  }


  /**
    * Add a new item.
    */
  def addItem : Action[AnyContent] = Action { implicit request =>
    itemForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.assortment(categoryForm, formWithErrors))
      },
      itemData => {
        services.ItemService.addItem(itemData.categoryID, itemData.name, itemData.price, itemData.visibility.getOrElse(false))
        Redirect(routes.AssortmentController.manageAssortment)
      })
  }

  /**
    * Update a specific category.
    */
  def updateCategory : Action[AnyContent] = Action { implicit request =>
    categoryForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.assortment(formWithErrors, itemForm))
      },
      categoryData => {
        val id = categoryData.id
        id match {
          case Some(id) =>
            if(CategoryService.nameInUse(id, categoryData.name)) {
              Redirect(routes.AssortmentController.manageAssortment).flashing("fail" -> "Kategoriename ist schon vergeben!")
            } else if(CategoryService.lastVisibleCategory(id) && !categoryData.visibility.getOrElse(false)) {
              Redirect(routes.AssortmentController.manageAssortment).flashing("fail" -> "Es muss mindestens eine sichtbare Kategorie geben!")
            } else {
              CategoryService.updateCategory(id, categoryData.name, categoryData.visibility.getOrElse(false))
              Redirect(routes.AssortmentController.manageAssortment).flashing(
                "success" -> "Die Kategorie wurde erfolgreich aktualisiert!")
            }
          case None => Redirect(routes.AssortmentController.manageAssortment).flashing("fail" -> "Ein Fehler ist aufgetreten!")
        }
      })
  }

  /**
    * Edit a specific item.
    */
  def rmCategory(categoryID: Option[Long]) : Action[AnyContent] = Action { request =>
    request.session.get("id").map { id =>
      val currentUser = UserService.getUserByID(id.toLong)
      currentUser match {
        case Some(currentUser) => if(currentUser.admin) {
                                    categoryID match {
                                      case Some(categoryID) =>
                                        if (CategoryService.lastVisibleCategory(categoryID)) {
                                          Redirect(routes.AssortmentController.manageAssortment).flashing(
                                            "fail" -> "Es muss mindestens eine sichtbare Kategorie geben!")
                                        } else {
                                          services.CategoryService.rmCategory(categoryID)
                                          Redirect(routes.AssortmentController.manageAssortment).flashing(
                                            "success" -> "Die Kategorie wurde erfolgreich gelöscht!")
                                        }
                                      case None => Redirect(routes.AssortmentController.manageAssortment)
                                    }
                                  } else Redirect(routes.Application.index)
        case None => Redirect(routes.Application.index)
      }
    }.getOrElse {
      Redirect(routes.Application.index)
    }
  }

  /**
    * Edit a specific item.
    */
  def editItem(ofItem: Option[Long]) : Action[AnyContent] = Action { request =>
    request.session.get("id").map { id =>
      val currentUser = UserService.getUserByID(id.toLong)
      currentUser match {
        case Some(currentUser) => ofItem match {
          case Some(ofItem) => val item = ItemService.getItem(ofItem)
            item match {
              case Some(item) => if(currentUser.admin) Ok(views.html.editItem(item)) else Redirect(routes.AssortmentController.manageAssortment)
              case None => Redirect(routes.AssortmentController.manageAssortment)
            }
          case None => Redirect(routes.AssortmentController.manageAssortment)
        }
        case None => Redirect(routes.UserController.logout)
      }
    }.getOrElse {
      Redirect(routes.Application.index)
    }
  }

  /**
    * Update a specific Item and go back to edit item view.
    */
  def updateItem() : Action[AnyContent] = Action { implicit request =>
    editItemForm.bindFromRequest.fold(
      formWithErrors => {
        Forbidden("Fehler")//BadRequest(views.html.editUser(None))
      },
      itemData => {
        val item = models.Item(itemData.id, itemData.categoryID, itemData.name, itemData.price, itemData.visibility.getOrElse(false))//, userData.password, userData.admin)
        ItemService.updateItem(item)
        Redirect(routes.AssortmentController.editItem(Some(item.id)))//"Successfully updated changes."
      })
  }

  /**
    * List all orders of user in the system.
    */
  def manageAssortment : Action[AnyContent] = Action { implicit request =>
    request.session.get("id").map { id =>
      val currentUser = UserService.getUserByID(id.toLong)
      currentUser match {
        case Some(currentUser) => if(currentUser.admin) Ok(views.html.assortment(categoryForm, itemForm)) else Redirect(routes.Application.index)
        case None => Redirect(routes.UserController.logout)
      }
    }.getOrElse {
      Redirect(routes.Application.index)
    }
  }

}
