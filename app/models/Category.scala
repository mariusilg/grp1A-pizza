package models

/**
  * Category entity.
  * @param id database id of the category.
  * @param name name of the category.
  * @param unit unit name of items in this category.
  * @param visibility visibility of the category for customers.
  */
case class Category(var id: Long, var name: String, var unit: String, var visibility: Boolean) {

  /**
    * Getter of unit.
    * @return unit as a String
   */
  def getUnit: String = this.unit
}


/**
  * Size entity.
  * @param id database id of the size.
  * @param name name of the size.
  * @param size size of the size.
  */
case class Size(var id: Long, var name: String, var size: Int, var categoryID: Long)