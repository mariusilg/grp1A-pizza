package models
/**
  * Category entity.
  * @param id database id of the category.
  * @param name name of the category.
  * @param unit unit of items in this category.
  * @param visibility visibility of the category for customers.
  */
//var preperationTime: Int,
case class Category(var id: Long, var name: String, var unit: String, var visibility: Boolean) {
  def getUnit: String = this.unit
}

case class Size(var id: Long, var name: String, var size: Int)