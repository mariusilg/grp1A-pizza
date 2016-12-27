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
    * Creates the given item in the database.
    * @param item the item object to be stored.
    * @return the persisted item object
    */
  def addItem(item: Item): Item = {
    DB.withConnection { implicit c =>
      val id: Option[Long] =
        SQL("insert into Items(cat_id, name, price, visibility) values ({cat_id}, {name}, {price}, {visibility})").on(
          'cat_id -> item.categoryID, 'name -> item.name, 'price -> item.price, 'visibility -> item.visibility).executeInsert()
      item.id = id.get
    }
    item
  }

  /**
    * Returns optional item from the database.
    * @return optional item object.
    */
  def getItem(id: Long): Option[Item] = {
    DB.withConnection { implicit c =>
      val selectItem = SQL("Select id, cat_id, name, price, visibility from Items where id = {id} limit 1;").on('id -> id).apply
        .headOption
      selectItem match {
        case Some(row) => Some(Item(row[Long]("id"), row[Long]("cat_id"), row[String]("name"), row[Int]("price"), row[Boolean]("visibility")))
        case None => None
      }
    }
  }

  /**
    * Returns a list of available items by category ID from the database.
    * @return a list of item objects.
    */
  def getItemsByCategory(id: Long): List[Item] = {
    DB.withConnection { implicit c =>
      val selectItemsByCategory= SQL("Select id, cat_id, name, price, visibility from Items where cat_id = {id};").on('id -> id)
      val itemsByCategory = selectItemsByCategory().map(row => Item(row[Long]("id"), row[Long]("cat_id"), row[String]("name"), row[Int]("price"), row[Boolean]("visibility"))).toList
      itemsByCategory
    }
  }

  /**
    * Updates an item from the database.
    * @return whether update was successful or not.
    */
  def updateItem(item: Item): Boolean = {
    DB.withConnection { implicit c =>
      val rowsUpdated = SQL("update Items SET cat_id={categoryID}, name={name}, price={price}, visibility={visibility} where id = {id}").on('categoryID -> item.categoryID, 'name -> item.name, 'price -> item.price, 'visibility -> item.visibility, 'id -> item.id).executeUpdate()
      rowsUpdated == 1
    }
  }

}

object ItemDao extends ItemDaoT
