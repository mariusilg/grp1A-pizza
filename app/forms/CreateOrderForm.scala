package forms

/**
  * Form containing data to create a order.
  * @param itemID id of the item.
  * @param quantity quantity of item.
  * @param size size of item.
  * @param eQuantity optional quantity of extra.
  * @param extraID optional id of extra.
  */
//case class CreateOrderForm(itemID: Long, quantity: Int, size: Int, eQuantity: Option[Int], extraID: Option[Long])

case class CreateOrderForm(itemID: Long, quantity: Int, size: Int, extraID: List[Long])