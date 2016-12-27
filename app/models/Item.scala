package models

case class Item(var id: Long, var categoryID: Long, var name: String, var price: Int, var visibility: Boolean) {

  def priceToString : String = {
    "%.2f €".format(this.price.toDouble/100)
  }

}