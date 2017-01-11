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
        SQL("insert into Items(cat_id, name, price, extra_flag, prep_duration, visibility) values ({cat_id}, {name}, {price}, {extra_flag}, {prep_duration}, {visibility})").on(
          'cat_id -> item.categoryID, 'name -> item.name, 'price -> item.price, 'extra_flag -> item.extrasFlag, 'prep_duration -> item.prepDuration, 'visibility -> item.visibility).executeInsert()
      item.id = id.get
    }
    item
  }

  /**
    *
    * Returns optional item from the database.
    * @param id id of the item.
    * @return optional item object
    */
  def getItem(id: Long): Option[Item] = {
    DB.withConnection { implicit c =>
      val selectItem = SQL("Select id, cat_id, name, price, extra_flag, prep_duration, visibility from Items where id = {id} limit 1;").on('id -> id).apply
        .headOption
      selectItem match {
        case Some(row) => Some(Item(row[Long]("id"), row[Long]("cat_id"), row[String]("name"), row[Int]("price"), row[Boolean]("extra_flag"), row[Int]("prep_duration"), row[Boolean]("visibility")))
        case None => None
      }
    }
  }

  /**
    * Returns a list of available items by category ID from the database.
    * @param id id of the item.
    * @return a list of item objects
    */
  def getItemsByCategory(id: Long): List[Item] = {
    DB.withConnection { implicit c =>
      val selectItemsByCategory = SQL("Select id, cat_id, name, price, extra_flag, prep_duration, visibility from Items where cat_id = {id};").on('id -> id)
      val itemsByCategory = selectItemsByCategory().map(row => Item(row[Long]("id"), row[Long]("cat_id"), row[String]("name"), row[Int]("price"), row[Boolean]("extra_flag"), row[Int]("prep_duration"), row[Boolean]("visibility"))).toList
      itemsByCategory
    }
  }

  /**
    * Returns a list of available items by category ID from the database.
    * @param id id of category.
    * @return a list of item objects
    */
  def getAvailableItemsByCategory(id: Long): List[Item] = {
    DB.withConnection { implicit c =>
      val selectItemsByCategory = SQL("Select id, cat_id, name, price, extra_flag, prep_duration from Items where visibility = TRUE and cat_id = {id};").on('id -> id)
      val itemsByCategory = selectItemsByCategory().map(row => Item(row[Long]("id"), row[Long]("cat_id"), row[String]("name"), row[Int]("price"), row[Boolean]("extra_flag"), row[Int]("prep_duration"), true)).toList
      itemsByCategory
    }
  }

  /**
    * Updates an item in the database.
    * @param item item object.
    * @return Boolean success-flag
    */
  def updateItem(item: Item): Boolean = {
    DB.withConnection { implicit c =>
      val rowsUpdated = SQL("update Items SET cat_id={categoryID}, name={name}, price={price}, extra_flag={extrasFlag}, prep_duration={prepDuration}, visibility={visibility} where id = {id}")
        .on('categoryID -> item.categoryID, 'name -> item.name, 'price -> item.price, 'extrasFlag -> item.extrasFlag, 'prepDuration -> item.prepDuration, 'visibility -> item.visibility, 'id -> item.id).executeUpdate()
      rowsUpdated == 1
    }
  }


  /**
    * Returns whether there is one visible Item (without this one) in Category left or not.
    * @param categoryID id of category.
    * @param id id of the item.
    * @return Boolean
    */
  def lastVisibleItemOfCategory(categoryID: Long, id: Long): Boolean = {
    DB.withConnection { implicit c =>
      val lastVisibleItem = SQL("Select COUNT(*) as cnt from Items where visibility = TRUE and cat_id = {categoryID} and id <> {id};").on('categoryID -> categoryID, 'id -> id).apply
        .headOption
      lastVisibleItem match {
        case Some(row) => row[Long]("cnt") == 0
        case None => true
      }
    }
  }

  /**
    * Returns whether username already in use by another item or not.
    * @param id id of the item.
    * @param name name.
    * @return Boolean
    */
  def nameInUse(id: Long, name: String): Boolean = {
    DB.withConnection { implicit c =>
      val checkAvailability = SQL("Select COUNT(*) as cnt from Items where UPPER(name) = UPPER({name}) and id <> {id};").on('name -> name, 'id -> id).apply
        .headOption
      checkAvailability match {
        case Some(row) => row[Long]("cnt") != 0
        case None => true
      }
    }
  }

  /**
    * Gets the default price of an item from the system.
    * @param id id of the item.
    * @return price of item
    */
  def getDefaultPrice(id: Long): Int = {
    DB.withConnection { implicit c =>
      val selectSize = SQL("Select price from Items where id = {id};").on('id -> id).apply
        .headOption
      selectSize match {
        case Some(row) => row[Int]("price")
        case None => 1
      }
    }
  }

  /**
    * Returns whether item has already been ordered or not.
    * @return Boolean.
    */
  def isItemDeletable(id: Long): Boolean = {
    DB.withConnection { implicit c =>
      val cntOrderItems = SQL("Select COUNT(*) as cnt from Order_items where item_id = {id};").on('id -> id).apply
        .headOption
      cntOrderItems match {
        case Some(row) => row[Long]("cnt") == 0
        case None => false
      }
    }
  }

  /**
    * Deactivates a item from the database.
    * @param id id of item.
    * @return a boolean success flag
    */
  def deactivateItem(id: Long): Boolean = {
    DB.withConnection { implicit c =>
      val rowsUpdated = SQL("update Items SET visibility = FALSE where id = {id}").on('id -> id).executeUpdate()
      rowsUpdated == 1
    }
  }

  /**
    * Removes a item by id from the database.
    * @param id the id of the item.
    * @return a boolean success flag
    */
  def rmItem(id: Long): Boolean = {
    DB.withConnection { implicit c =>
      val rowsCount = SQL("delete from Items where id = ({id})").on('id -> id).executeUpdate()
      rowsCount > 0
    }
  }




  /**
    * Returns amount of visible items by category registered in the system.
    * @param categoryID id of category.
    * @return amount
    */
  def cntVisibleItems(categoryID: Long): Long = {
    DB.withConnection { implicit c =>
      val itemCount = SQL("Select COUNT(*) as  cnt from Items where cat_id = {id} and visibility = TRUE").on('id -> categoryID).apply
        .headOption
      itemCount match {
        case Some(row) => row[Long]("cnt")
        case None => 0
      }
    }
  }

}

object ItemDao extends ItemDaoT
