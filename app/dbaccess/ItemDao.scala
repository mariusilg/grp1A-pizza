package dbaccess

import anorm._
import play.api.Play.current
import play.api.db.DB
import anorm.NamedParameter.symbol
import models._

/**
 * Data access object for item related operations.
 *
 * @author ne
 */
trait ItemDaoT {
  /**
    * Returns item from the database.
    * @return item.
    */
  def getItem(id: Long): Option[Item] = {
    DB.withConnection { implicit c =>
      val selectItem = SQL("Select * from Items where id = {id} limit 1;").on('id -> id).apply
        .headOption
      selectItem match {
        case Some(row) => Some(Item(row[Long]("id"), row[String]("name"), row[Int]("price")))
        case None => None
      }
    }
  }


}

object ItemDao extends ItemDaoT
