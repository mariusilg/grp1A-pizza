package dbaccess

import anorm._
import play.api.Play.current
import play.api.db.DB
import anorm.NamedParameter.symbol
import models._

/**
 * Data access object for extra item related operations.
 *
 * @author ne
 */
trait ExtraDaoT {
  /**
    * Returns extra item from the database.
    * @return item.
    */
  def getExtra(id: Long): Option[Item] = {
    DB.withConnection { implicit c =>
      val selectExtra = SQL("Select * from Extras where id = {id} limit 1;").on('id -> id).apply
        .headOption
      selectExtra match {
        case Some(row) => Some(Item(row[Long]("id"), 0, row[String]("name"), row[Int]("price"), false, 0, true))
        case None => None
      }
    }
  }

  /**
    * Returns a list of available extra items from the database.
    * @return a list of item objects.
    */
  def getExtras: List[Item] = {
    DB.withConnection { implicit c =>
      val selectExtras= SQL("Select id, name, price from Extras;")
      val extras = selectExtras().map(row => Item(row[Long]("id"), 0, row[String]("name"), row[Int]("price"), false, 0, true)).toList
      extras
    }
  }

}

object ExtraDao extends ExtraDaoT
