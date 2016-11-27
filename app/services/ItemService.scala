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

}

object ItemService extends ItemServiceT
