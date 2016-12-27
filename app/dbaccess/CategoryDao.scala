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
    * Returns a user from the database.
    * @return user.
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
        SQL("insert into Categories(name, visibility) values ({name}, {visibility})").on(
          'name -> category.name, 'visibility -> category.visibility).executeInsert()
      category.id = id.get
    }
    category
  }

  /**
    * Updates a category from the database.
    * @return whether update was successful or not.
    */
  def updateCategory(category: Category): Boolean = {
    DB.withConnection { implicit c =>
      val rowsUpdated = SQL("update Categories SET name={name}, visibility={visibility} where id = {id}").on('name -> category.name, 'visibility -> category.visibility, 'id -> category.id).executeUpdate()
      rowsUpdated == 1
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
    * Returns a list of available categories from the database.
    * @return a list of category objects.
    */
  def availableCategories: List[Category] = {
    DB.withConnection { implicit c =>
      val selectCategory= SQL("Select id, name, visibility from Categories;")
      val categories = selectCategory().map(row => Category(row[Long]("id"), row[String]("name"), row[Boolean]("visibility"))).toList
      categories
    }
  }

  /**
    * Returns a list of available categories from the database.
    * @return a list of category objects.
    */
  def visibleCategories: List[Category] = {
    DB.withConnection { implicit c =>
      val selectCategory= SQL("Select id, name from Categories where visibility = true;")
      val categories = selectCategory().map(row => Category(row[Long]("id"), row[String]("name"), true)).toList
      categories
    }
  }

  def getCategory(id: Long): Option[Category] = {
    DB.withConnection { implicit c =>
      val selectCategory = SQL("Select id, name, visibility from Categories where id = {id} limit 1;").on('id -> id).apply
        .headOption
      selectCategory match {
        case Some(row) => Some(Category(row[Long]("id"), row[String]("name"), row[Boolean]("visibility")))
        case None => None
      }
    }
  }

}

object CategoryDao extends CategoryDaoT
