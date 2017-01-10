package models

case class Item(var id: Long, var categoryID: Long, var name: String, var price: Int, var extrasFlag: Boolean, var prepDuration: Int, var visibility: Boolean) {

  def priceToString : String = {
    "%.2f €".format(this.price.toDouble/100)
  }

  def priceToString(size: Int) : String = {
    "%.2f €".format(size*this.price.toDouble/100)
  }

  def hasExtras: Boolean = this.extrasFlag

}