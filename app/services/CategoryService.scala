package services

import dbaccess.{CategoryDao, CategoryDaoT}
import models._

/**
  * Service class for category related operations.
  *
  * @author ne
  */
trait CategoryServiceT {

  val categoryDao: CategoryDaoT = CategoryDao

  /**
    * Adds a new category to the system.
    * @param name name of the new category.
    * @return the new category.
    */
  def addCategory(name: String, visibility: Boolean): Category = {
    // create Category
    val newCategory = Category(-1, name, visibility)
    // persist and return Category
    categoryDao.addCategory(newCategory)
  }

  /**
    * Return whether category exists or not.
    * @return nothing.
    */
  def updateCategory(id: Long, name: String, visibility: Boolean): Unit = {
    val category = Category(id, name, visibility)
    categoryDao.updateCategory(category)
  }

  /**
    * Removes a category by id from the system.
    * @param id id of category.
    * @return a boolean success flag.
    */
  def rmCategory(id: Long): Boolean = categoryDao.rmCategory(id)

  /**
    * Gets a list of all available categories.
    * @return list of categories.
    */
  def availableCategories: List[Category] = {
    categoryDao.availableCategories
  }

  /**
    * Gets a list of all visible categories.
    * @return list of categories.
    */
  def visibleCategories: List[Category] = {
    categoryDao.visibleCategories
  }

  def getCategory(id: Long): Option[Category] = {
    categoryDao.getCategory(id)
  }

}

object CategoryService extends CategoryServiceT
