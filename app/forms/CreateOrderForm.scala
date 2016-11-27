package forms

/**
  * Form containing data to create a order.
  * @param itemID id of the item.
  * @param quantity quantity of item.
  * @param size size of item
  * @param costs costs of order
  */
case class CreateOrderForm(itemID: Long, quantity: Int, size: Int, costs: Int)