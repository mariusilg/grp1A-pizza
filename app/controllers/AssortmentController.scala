package controllers

import play.api.mvc.{Action, AnyContent, Controller}
import play.api.data.Forms._
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
      "ExtrasFlag" -> boolean,
      "PrepDuration" -> optional(number),
      "Visibility" -> optional(boolean)
    )(CreateItemForm.apply)(CreateItemForm.unapply))

  val editItemForm = Form(
    mapping(
      "ID" -> longNumber,
      "CategoryID" -> longNumber,
      "Name" -> text,
      "Price" -> number,
      "ExtrasFlag" -> boolean,
      "PrepDuration" -> optional(number),
      "Visibility" -> optional(boolean)
    )(EditItemForm.apply)(EditItemForm.unapply))

  val categoryForm = Form(
    mapping(
      "ID" -> optional(longNumber),
      "Name" -> text,
      "Unit" -> text,
      "Visibility" -> optional(boolean)
    )(CreateCategoryForm.apply)(CreateCategoryForm.unapply))

  /**
    * Add a new category.
    */
  def addCategory : Action[AnyContent] = Action { implicit request =>
    categoryForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.assortment(formWithErrors, itemForm))
      },
      categoryData => {
        services.CategoryService.addCategory(categoryData.name, categoryData.unit, categoryData.visibility.getOrElse(false))
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
              CategoryService.updateCategory(id, categoryData.name, categoryData.unit, categoryData.visibility.getOrElse(false))
              Redirect(routes.AssortmentController.manageAssortment).flashing(
                "success" -> "Die Kategorie wurde erfolgreich aktualisiert!")
            }
          case None => Redirect(routes.AssortmentController.manageAssortment).flashing("fail" -> "Ein Fehler ist aufgetreten!")
        }
      })
  }

  /**
    * Remove a specific category.
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
                                        } else if(!CategoryService.isCategoryDeletable(categoryID)) {
                                          CategoryService.deactivateCategory(categoryID)
                                          Redirect(routes.AssortmentController.manageAssortment).flashing(
                                            "success" -> "Die Kategorie wurde deaktiviert, da sie Produkte enthält die in vorhandenen Bestellungen auftauchen!")
                                        } else {
                                          CategoryService.rmCategory(categoryID)
                                          Redirect(routes.AssortmentController.manageAssortment).flashing(
                                            "success" -> "Die Kategorie wurde erfolgreich gelöscht!")
                                        }
                                      case None => Redirect(routes.AssortmentController.manageAssortment)
                                    }
                                  } else Redirect(routes.Application.index)
        case None => Redirect(routes.Auth.logout)
      }
    }.getOrElse {
      Redirect(routes.Application.index)
    }
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
        if (ItemService.nameInUse(0, itemData.name)) {
          Redirect(routes.AssortmentController.manageAssortment).flashing("fail" -> "Produktname schon vergeben")
        } else {
          services.ItemService.addItem(itemData.categoryID, itemData.name, itemData.price, itemData.extraFlag, itemData.prepDuration.getOrElse(0), itemData.visibility.getOrElse(false))
          Redirect(routes.AssortmentController.manageAssortment)
        }
      })
  }

  /**
    * Edit a specific item.
    */
  def editItem(ofItem: Option[Long]) : Action[AnyContent] = Action { implicit request =>
    request.session.get("id").map { id =>
      val currentUser = UserService.getUserByID(id.toLong)
      currentUser match {
        case Some(currentUser) => ofItem match {
                                    case Some(ofItem) => val item = ItemService.getItem(ofItem)
                                      item match {
                                        case Some(item) => if (currentUser.admin) Ok(views.html.editItem(item)) else Redirect(routes.Application.error).flashing("error" -> "Kein Zutritt zu diesem Bereich")
                                        case None => if (currentUser.admin) Redirect(routes.AssortmentController.manageAssortment).flashing("fail" -> "Item konnte nicht im System gefunden werden") else Redirect(routes.Application.error).flashing("error" -> "Kein Zutritt zu diesem Bereich")
                                      }
                                    case None => if (currentUser.admin) Redirect(routes.AssortmentController.manageAssortment) else Redirect(routes.Application.error).flashing("error" -> "Kein Zutritt zu diesem Bereich")
                                  }
        case None => Redirect(routes.Auth.logout)
      }
    }.getOrElse {
      Redirect(routes.Application.index)
    }
  }

  /**
    * Remove a specific item.
    */
  def rmItem(itemID: Option[Long]) : Action[AnyContent] = Action { request =>
    request.session.get("id").map { id =>
      val currentUser = UserService.getUserByID(id.toLong)
      currentUser match {
        case Some(currentUser) => if(currentUser.admin) {
          itemID match {
            case Some(itemID) =>
              if(!services.ItemService.isItemDeletable(itemID)) {
                ItemService.deactivateItem(itemID)
                Redirect(routes.AssortmentController.manageAssortment).flashing(
                  "fail" -> "Das Produkt wurde deaktiviert, da es in vorhandenen Bestellungen auftaucht!")
              } else {
                ItemService.rmItem(itemID)
                Redirect(routes.AssortmentController.manageAssortment).flashing(
                  "success" -> "Das Item wurde erfolgreich gelöscht!")
              }
            case None => Redirect(routes.AssortmentController.manageAssortment)
          }
        } else Redirect(routes.Application.error).flashing("error" -> "Ihnen fehlen Berechtigungen")
        case None => Redirect(routes.Auth.logout)
      }
    }.getOrElse {
      Redirect(routes.Application.index)
    }
  }

  /**
    * Update a specific Item.
    */
  def updateItem() : Action[AnyContent] = Action { implicit request =>
    editItemForm.bindFromRequest.fold(
      formWithErrors => {
        Redirect(routes.Application.error()).flashing("error" -> "Fehler bei Update")
      },
      itemData => {
        val item = models.Item(itemData.id, itemData.categoryID, itemData.name, itemData.price, itemData.extraFlag, itemData.prepDuration.getOrElse(0), itemData.visibility.getOrElse(false))
        if (ItemService.nameInUse(item.id, item.name)) {
          Redirect(routes.AssortmentController.editItem(Some(item.id))).flashing("fail" -> "Produktname ist schon vergeben!")
        //} else if (ItemService.lastVisibleItemOfCategory(item.categoryID, item.id) && !itemData.visibility.getOrElse(false)) {
        //  Redirect(routes.AssortmentController.editItem(Some(item.id))).flashing("fail" -> "Kategorie muss mindestens ein aktives Produkt besitzten")
        } else {
          ItemService.updateItem(item)
          Redirect(routes.AssortmentController.editItem(Some(item.id))).flashing("success" -> "Produkt wurde erfolgreich aktualisiert")
        }
      })
  }

  /**
    * Manage all categories, products and extras.
    */
  def manageAssortment : Action[AnyContent] = Action { implicit request =>
    request.session.get("id").map { id =>
      val currentUser = UserService.getUserByID(id.toLong)
      currentUser match {
        case Some(currentUser) => if(currentUser.admin) Ok(views.html.assortment(categoryForm, itemForm)) else Redirect(routes.Application.index)
        case None => Redirect(routes.Auth.logout)
      }
    }.getOrElse {
      Redirect(routes.Application.index)
    }
  }

}
