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
    * Returns extra item from the database by id.
    * @param id id of extra.
    * @return optional item object.
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

  def updateExtra(extra: Item): Item = {
    println("hallihallo wie gehts denn so")
    DB.withConnection { implicit c =>
      val id: Int =
        SQL("update Extras set id = {id}, cat_id = {cat_id}, name = {name}, price = {price}").on(
          'id -> extra.id.toInt,
          'cat_id -> extra.categoryID.toInt,
          'name -> extra.name,
          'price -> extra.price
        ).executeUpdate()
    }
    extra
  }

  def insertExtra(extra: Item): Item = {
    DB.withConnection { implicit c =>
      val id: Option[Long] =
        SQL("insert into Extras(cat_id, name, price) values({cId}, {name}, {price})").on(
          'cId -> extra.categoryID,
          'name -> extra.name,
          'price -> extra.price
        ).executeInsert()
    }
    extra
  }

  /**
    * Returns a list of all extra items from the database.
    * @return a list of item objects.
    */
  def getExtras: List[Item] = {
    DB.withConnection { implicit c =>
      val selectExtras= SQL("Select id, name, cat_id, price from Extras;")
      val extras = selectExtras().map(row => Item(row[Long]("id"), row[Int]("cat_id"), row[String]("name"), row[Int]("price"), false, 0, true)).toList
      extras
    }
  }

  def getExtrasForCategory(cat_id: Long): List[Item] = {
    DB.withConnection { implicit c =>
      val selectExtras= SQL("Select id, name, cat_id, price from Extras where cat_id = {cat_id};").on('cat_id -> cat_id)
      val extras = selectExtras().map(row => Item(row[Long]("id"), row[Int]("cat_id"), row[String]("name"), row[Int]("price"), false, 0, true)).toList
      extras
    }
  }

  def getExtrasById(categoryId: Long): List[Item] = {
    println(categoryId)
    DB.withConnection { implicit c =>
      val selectExtras= SQL("Select id, name, cat_id, price from Extras where cat_id = {categoryId};").on('categoryId -> categoryId)
      val extras = selectExtras().map(row => Item(row[Long]("id"), row[Int]("cat_id"), row[String]("name"), row[Int]("price"), false, 0, true)).toList
      extras
    }
  }

  def rmExtra(extraId: Int): String = {
    DB.withConnection { implicit c =>
      val selectExtras= SQL("delete from Extras where id = {extraId};").on('extraId -> extraId).executeUpdate()
      "true"
    }
  }

}

object ExtraDao extends ExtraDaoT
