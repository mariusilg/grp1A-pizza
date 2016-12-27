package models
/**
  * Category entity.
  * @param id database id of the category.
  * @param name name of the category.
  * @param visibility visibility of the category for customers.
  */
case class Category(var id: Long, var name: String, var visibility: Boolean)