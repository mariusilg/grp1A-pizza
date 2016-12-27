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
  def addItem(categoryID: Long, name: String, price: Int, visibility: Boolean): Item = {
    // create Item
    val newItem = Item(-1, categoryID, name, price, visibility)
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
    * Updates object item in database.
    * @return nothing.
    */
  def updateItem(item: Item): Unit = {
    itemDao.updateItem(item)
  }

}

object ItemService extends ItemServiceT
