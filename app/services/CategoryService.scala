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
    * @param visibility visibility of the new category.
    * @return the new category.
    * getestet
    */
  def addCategory(name: String, unit: String, visibility: Boolean): Category = {
    // create Category
    val newCategory = Category(-1, name, unit, visibility)
    // persist and return Category
    categoryDao.addCategory(newCategory)
  }

  /**
    * Method to update category.
    * @param id id of the category
    * @param name name of the category.
    * @param visibility visibility of the category.
    * getestet
    */
  def updateCategory(id: Long, name: String, unit: String, visibility: Boolean): Unit = {
    val category = Category(id, name, unit, visibility)
    categoryDao.updateCategory(category)

  }

  /**
    * Removes a category by id from the system.
    * @param id id of category.
    * @return a boolean success flag.
    * getestet
    */
  def rmCategory(id: Long): Boolean = categoryDao.rmCategory(id)

  /**
    * Gets a list of all available categories from the system.
    * @return list of categories.
    * muss ich das Testen, wenn ich mir vorher Liste schon ausgeben hab lassen?
    * getestet
    */
  def availableCategories: List[Category] = {
    categoryDao.availableCategories
  }

  /**
    * Gets a list of all visible categories from the system.
    * @return list of categories.
    * getestet
    */
  def visibleCategories: List[Category] = {
    categoryDao.visibleCategories
  }

  /**
    * Gets sizes of a category from the system
    * @return list of sizes
    *getestet
    */
  def getSizes(id: Long): List[Size] = categoryDao.getSizes(id)

  /**
    * Gets the default size of a category from the system.
    * @return size.
    * getestet
    */
  def getDefaultSize(id: Long): Int = categoryDao.getDefaultSize(id)

  /**
    * Return whether there category has specific sizes or not.
    * @return boolean.
    * getestet
    */
  def hasSizes(id: Long) : Boolean = categoryDao.hasSizes(id)

  /**
    * Return unit name by id of category.
    * @return unit name as a String.
    * getestet
    */
  def getUnit(id: Long) : String = categoryDao.getUnit(id)

  /**
    * Return whether there is only one visible category left or not.
    * @return boolean.
    * ToDo: mit Nils drüber reden
    */
  def lastVisibleCategory(id: Long) : Boolean = categoryDao.lastVisibleCategory(id)

  /**
    * Return whether new name of category exists or not.
    * @return true or false.
    * ToDo: mit Nils drüber reden
    */
  def nameInUse(id: Long, name: String): Boolean = categoryDao.nameInUse(id, name)

  /**
    * Gets a specific category from the system.
    * @return category id.
    * getestet
    */
  def getCategory(id: Long): Option[Category] = {
    categoryDao.getCategory(id)
  }

  /**
    * Returns the first visible category id from the system.
    * @return optional category id.
    * getestet
    */
  def getDefaultCategory: Option[Long] = categoryDao.getDefaultCategory

  /**
    * Checks whether the category is visible or not.
    * @param id id of category.
    * @return a boolean flag.
    * getestet
    */
  def isCategoryVisible(id: Long): Boolean = categoryDao.isCategoryVisible(id)

  /**
    * Checks whether a category is deletable from the system.
    * @param id id of category.
    * @return a boolean.
    */
  def isCategoryDeletable(id: Long): Boolean = categoryDao.isCategoryDeletable(id)

  /**
    * Deactivates a category by id from the system.
    * @param id category id.
    * @return a boolean success flag.
    */
  def deactivateCategory(id: Long): Boolean = categoryDao.deactivateCategory(id)

}

object CategoryService extends CategoryServiceT
