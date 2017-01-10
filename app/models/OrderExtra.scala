package models

case class OrderExtra(var id: Long, var name: String, var quantity: Int, var price: Int) {

  /*
   * Method to convert this price into a String.
   */
  def priceToString : String = {
    "%.2f €".format(this.price.toDouble/100)
  }

}