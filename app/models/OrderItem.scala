package models
/**
  * OrderItem entity.
  *
  *
  */
case class OrderItem(var id: Long, var name: String, var quantity: Int, size: Int, unit: String, var orderExtras: List[OrderExtra], var price: Int) {

  /*
   * Method to convert this price into a String.
   */
  def priceToString : String = {
    "%.2f €".format(this.price.toDouble/100)
  }

}