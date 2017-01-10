package forms

/**
  * Form containing data to update a item.
  * @param id id of the item.
  * @param categoryID category id of the item.
  * @param name name of the item.
  * @param price price of item.
  * @param extraFlag boolean extraFlag of item
  * @param prepDuration optional duration of preparation of item.
  * @param visibility optional visibility-flag of item
  */
case class EditItemForm(id: Long, categoryID: Long, name: String, price: Int, extraFlag: Boolean, prepDuration: Option[Int], visibility: Option[Boolean])