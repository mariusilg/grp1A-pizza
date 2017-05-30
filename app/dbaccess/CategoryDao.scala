package dbaccess

import anorm._
import play.api.Play.current
import play.api.db.DB
import anorm.NamedParameter.symbol
import models._

/**
  * Data access object for category related operations.
  *
  * @author ne
  */
trait CategoryDaoT {

  /**
    * Returns whether name is in use or not.
    * @return boolean
    */
  def checkName(name: String): Boolean = {
    DB.withConnection { implicit c =>
      val checkName = SQL("Select COUNT(*) as cnt from Categories where UPPER(name) = UPPER({name});").on('name -> name).apply
        .headOption
      checkName match {
        case Some(row) => row[Long]("cnt") == 1
        case None => false
      }
    }
  }


  /**
    * Creates the given category in the database.
    * @param category the category object to be stored.
    * @return the persisted category object
    */
  def addCategory(category: Category): Category = {
    DB.withConnection { implicit c =>
      val id: Option[Long] =
        SQL("insert into Categories(name, unit, visibility) values ({name}, {unit}, {visibility})").on(
          'name -> category.name, 'unit -> category.unit, 'visibility -> category.visibility).executeInsert()
      category.id = id.get
    }
    category
  }

  /**
    * Updates a category from the database.
    * @return whether update was successful or not
    */
  def updateCategory(category: Category): Boolean = {
    DB.withConnection { implicit c =>
      val rowsUpdated = SQL("update Categories SET name={name}, visibility={visibility} where id = {id}").on('name -> category.name, 'visibility -> category.visibility, 'id -> category.id).executeUpdate()
      rowsUpdated == 1
    }
  }

  /**
    * Returns whether products of category had been ordered yet.
    * @return Boolean
    */
  def isCategoryDeletable(id: Long): Boolean = {
    DB.withConnection { implicit c =>
      val cntProducts = SQL("Select COUNT(*) as cnt from Order_items o, Items i where i.cat_id = {id} and o.item_id = i.id;").on('id -> id).apply
        .headOption
      cntProducts match {
        case Some(row) => row[Long]("cnt") == 0
        case None => false
      }
    }
  }

  /**
    * Removes a category by id from the database.
    * @param id the id of the category
    * @return a boolean success flag
    */
  def rmCategory(id: Long): Boolean = {
    DB.withConnection { implicit c =>
      val rowsCount = SQL("delete from Categories where id = ({id})").on('id -> id).executeUpdate()
      rowsCount > 0
    }
  }

  /**
    * Returns a list of all categories from the database.
    * @return a list of category objects.
    */
  def availableCategories: List[Category] = {
    DB.withConnection { implicit c =>
      val selectCategory= SQL("Select id, name, unit, visibility from Categories;")
      val categories = selectCategory().map(row => Category(row[Long]("id"), row[String]("name"), row[String]("unit"), row[Boolean]("visibility"))).toList
      categories
    }
  }

  /**
    * Returns a list of visible categories from the database.
    * @return a list of category objects
    */
  def visibleCategories: List[Category] = {
    DB.withConnection { implicit c =>
      val selectCategory= SQL("Select id, name, unit from Categories where visibility = true;")
      val categories = selectCategory().map(row => Category(row[Long]("id"), row[String]("name"), row[String]("unit"), true)).toList
      categories
    }
  }

  /**
    * Returns a list of available sizes from the database.
    * @return a list of size objects
    */
  def getSizes(id: Long): List[Size] = {
    DB.withConnection { implicit c =>
      val selectSizes = SQL("Select id, name, size from Sizes where cat_id = {id};").on('id -> id)
      val sizes = selectSizes().map(row => Size(row[Long]("id"), row[String]("name"), row[Int]("size"))).toList
      sizes
    }
  }

  /**
    * Gets the default size of item of category from the system.
    * @param id id of category.
    * @return size
    */
  def getDefaultSize(id: Long): Int = {
    DB.withConnection { implicit c =>
      val selectSize = SQL("Select size from Sizes where cat_id = {id} limit 1;").on('id -> id).apply
        .headOption
      selectSize match {
        case Some(row) => row[Int]("size")
        case None => 1
      }
    }
  }

  /**
    * Returns whether category has specific sizes defined or not.
    * @param id id of category.
    * @return Boolean
    */
  def hasSizes(id: Long): Boolean = {
    DB.withConnection { implicit c =>
      val cntSizes = SQL("Select COUNT(*) as cnt from Sizes where cat_id = {id};").on('id -> id).apply
        .headOption
      cntSizes match {
        case Some(row) => row[Long]("cnt") > 0
        case None => false
      }
    }
  }

  /**
    * Returns unit name of specific category.
    * @param id id of category.
    * @return unit name as a String
    */
  def getUnit(id: Long): String = {
    DB.withConnection { implicit c =>
      val unit = SQL("Select unit from Categories where id = {id};").on('id -> id).apply
        .headOption
      unit match {
        case Some(row) => row[String]("unit")
        case None => return ""
      }
    }
  }

  /**
    * Returns whether there is one other visible category left or not.
    * @param id id of category.
    * @return Boolean
    */
  def lastVisibleCategory(id: Long): Boolean = {
    DB.withConnection { implicit c =>
      val cntVisible = SQL("Select COUNT(*) as cnt from Categories where visibility = TRUE and id <> {id};").on('id -> id).apply
        .headOption
      cntVisible match {
        case Some(row) => row[Long]("cnt") == 0
        case None => true
      }
    }
  }

  /**
    * Returns whether username already in use or not.
    * @param id id of category.
    * @param name new name of category.
    * @return Boolean.
    */
  def nameInUse(id: Long, name: String): Boolean = {
    DB.withConnection { implicit c =>
      val checkAvailability = SQL("Select COUNT(*) as cnt from Categories where UPPER(name) = UPPER({name}) and id <> {id};").on('name -> name, 'id -> id).apply
        .headOption
      checkAvailability match {
        case Some(row) => row[Long]("cnt") != 0
        case None => true
      }
    }
  }


  /**
    * Returns the first visible category from the database.
    * @return optional id of category.
    */
  def getDefaultCategory: Option[Long] = {
    DB.withConnection { implicit c =>
      val selectCategory= SQL("Select id from Categories where visibility = true limit 1;").apply
      .headOption
      selectCategory match {
        case Some(row) => Some((row[Long]("id")))
        case None => None
      }
    }
  }

  /**
    * Returns category from the database by id.
    * @return optional object category
    */
  def getCategory(id: Long): Option[Category] = {
    DB.withConnection { implicit c =>
      val selectCategory = SQL("Select id, name, unit, visibility from Categories where id = {id} limit 1;").on('id -> id).apply
        .headOption
      selectCategory match {
        case Some(row) => Some(Category(row[Long]("id"), row[String]("name"), row[String]("unit"), row[Boolean]("visibility")))
        case None => None
      }
    }
  }

  def getCategories(): List[Category] = {
    DB.withConnection { implicit c =>
      val selectCategories = SQL("Select id, name, unit, visibility from Categories;")
      val categories = selectCategories().map(row => Category(row[Long]("id"), row[String]("name"), row[String]("unit"), row[Boolean]("visibility"))).toList
      categories
    }
  }

  /**
    * Checks whether category is visible or not.
    * @param id id of category.
    * @return boolean
    */
  def isCategoryVisible(id: Long): Boolean = {
    DB.withConnection { implicit c =>
      val checkVisibility = SQL("Select COUNT(*) as cnt from Categories where visibility = TRUE and id = {id};").on('id -> id).apply
        .headOption
      checkVisibility match {
        case Some(row) => row[Long]("cnt") == 1
        case None => false
      }
    }
  }

  /**
    * Deactivates a category from the database by id.
    * @param id id of category.
    * @return whether update was successful or not
    */
  def deactivateCategory(id: Long): Boolean = {
    DB.withConnection { implicit c =>
      val rowsUpdated = SQL("update Categories SET visibility = FALSE where id = {id}").on('id -> id).executeUpdate()
      rowsUpdated == 1
    }
  }

}

object CategoryDao extends CategoryDaoT
