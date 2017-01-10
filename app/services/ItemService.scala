package services

import dbaccess.{ItemDao, ItemDaoT}
import models._

/**
 * Service class for item related operations.
 *
 * @author ne
 */
trait ItemServiceT {

  val itemDao: ItemDaoT = ItemDao

  /**
    * Adds a new item to the system.
    * @param categoryID category id of the new item.
    * @param name name of the new item.
    * @param price price of the new item.
    * @return the new item.
    */
  def addItem(categoryID: Long, name: String, price: Int, extraFlag: Boolean, prepDuration: Int, visibility: Boolean): Item = {
    // create Item
    val newItem = Item(-1, categoryID, name, price, extraFlag, prepDuration, visibility)
    // persist and return Item
    itemDao.addItem(newItem)
  }

  /**
    * Return item by id.
    * @return item.
    */
  def getItem(id: Long): Option[Item] = {
    itemDao.getItem(id)
  }

  /**
    * Return a list of items by catergory id.
    * @return list of items.
    */
  def getItemsByCategory(id: Long): List[Item] = {
    itemDao.getItemsByCategory(id)
  }

  /**
    * Return a list of visible items by catergory id.
    * @return list of items.
    */
  def getAvailableItemsByCategory(id: Long): List[Item] = {
    itemDao.getAvailableItemsByCategory(id)
  }

  /**
    * Return whether there is only one visible item left in category or not.
    * @return boolean.
    */
  def lastVisibleItemOfCategory(categoryID: Long, id: Long) : Boolean = itemDao.lastVisibleItemOfCategory(categoryID, id)

  /**
    * Updates object item in database.
    * @return nothing.
    */
  def updateItem(item: Item): Unit = {
    itemDao.updateItem(item)
  }

  /**
    * Return whether new itemname exists or not.
    * @return true or false.
    */
  def nameInUse(id: Long, name: String): Boolean = itemDao.nameInUse(id, name)

  /**
    * Checks whether a item is deletable from the system.
    * @param id id of item.
    * @return boolean.
    */
  def isItemDeletable(id: Long): Boolean = itemDao.isItemDeletable(id)

  /**
    * Deactivates a item by id from the system.
    * @param id item id.
    * @return a boolean success flag.
    */
  def deactivateItem(id: Long): Boolean = itemDao.deactivateItem(id)

  /**
    * Removes a item by id from the system.
    * @param id id of item.
    * @return a boolean success flag.
    */
  def rmItem(id: Long): Boolean = itemDao.rmItem(id)

  /*##########################################################################
  ##                          REPORTING                                     ##
  ##########################################################################*/

  /**
    * Active item count by category.
    */
  def cntVisibleItems(id: Long) : Long = itemDao.cntVisibleItems(id)

}

object ItemService extends ItemServiceT
