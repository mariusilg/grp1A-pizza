package forms

/**
  * Form containing data to create an order.
  * @param itemID id of the item.
  * @param quantity quantity of item.
  * @param size size of item.
  * @param extraID id list of extras.
  */

case class CreateOrderForm(itemID: Long, quantity: Int, size: Int, extraID: List[Long])