package services

import dbaccess.{ExtraDao, ExtraDaoT}
import models._

/**
 * Service class for extra item related operations.
 *
 * @author ne
 */
trait ExtraServiceT {

  val extraDao: ExtraDaoT = ExtraDao

  /**
    * Return extra by id.
    * @return item.
    */
  def getExtra(id: Long): Option[Item] = {
    extraDao.getExtra(id)
  }

  /**
    * Return a list of extras.
    * @return list of items.
    */
  def getExtras: List[Item] = {
    extraDao.getExtras
  }

}

object ExtraService extends ExtraServiceT
