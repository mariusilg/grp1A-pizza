package forms

/**
  * Form containing data to create an item.
  * @param name name of the item.
  * @param categoryID category id of the item.
  * @param price price of item.
  * @param visibility visibility of item.
  */
case class CreateItemForm(categoryID: Long, name: String, price: Int, visibility: Option[Boolean])