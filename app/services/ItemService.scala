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

}

object ItemService extends ItemServiceT
