package forms

/**
  * Form containing data to create an item.
  * @param categoryID category id of the item.
  * @param name name of the item.
  * @param price price of item.
  * @param extraFlag extra-flag of item.
  * @param prepDuration optional preparation duration of item.
  * @param visibility optional visibility-flag of item.
  */
case class CreateItemForm(categoryID: Long, name: String, price: Int, extraFlag: Boolean, prepDuration: Option[Int], visibility: Option[Boolean])