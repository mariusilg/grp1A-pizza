package dbaccess

import anorm._
import play.api.Play.current
import play.api.db.DB
import anorm.NamedParameter.symbol
import models._

/**
 * Data access object for user related operations.
 *
 * @author ob, scsn, ne
 */
trait UserDaoT {

  /**
   * Creates the given user in the database.
   * @param user the user object to be stored.
   * @return the persisted user object
   */
  def addUser(user: User): User = {
    DB.withConnection { implicit c =>
      val id: Option[Long] =
        SQL("insert into Users(name) values ({name})").on(
          'name -> user.name).executeInsert()
      user.id = id.get
    }
    user
  }

  /**
   * Removes a user by id from the database.
   * @param id the users id
   * @return a boolean success flag
   */
  def rmUser(id: Long): Boolean = {
    DB.withConnection { implicit c =>
      val rowsCount = SQL("delete from Users where id = ({id})").on('id -> id).executeUpdate()
      rowsCount > 0
    }
  }

  /**
   * Returns a list of available user from the database.
   * @return a list of user objects.
   */
  def registeredUsers: List[User] = {
    DB.withConnection { implicit c =>
      val selectUsers = SQL("Select id, name, admin_flag from Users;")
      // Transform the resulting Stream[Row] to a List[(String,String)]
      val users = selectUsers().map(row => User(row[Long]("id"), row[String]("name"), row[Boolean]("admin_flag"))).toList
      users
    }
  }

  /**
    * Returns a list of available customers from the database.
    * @return a list of user objects.
    */
  def registeredCustomers: List[User] = {
    DB.withConnection { implicit c =>
      val selectCustomers = SQL("Select id, name, admin_flag from Users where admin_flag = false;")
      // Transform the resulting Stream[Row] to a List[(String,String)]
      val customers = selectCustomers().map(row => User(row[Long]("id"), row[String]("name"), row[Boolean]("admin_flag"))).toList
      customers
    }
  }

  /**
    * Returns a user from the database.
    * @return user.
    */
  def getUser(name: String): Option[User] = {
    DB.withConnection { implicit c =>
        val selectUser = SQL("Select * from Users where name = {name} limit 1;").on('name -> name).apply
          .headOption
        selectUser match {
          case Some(row) => Some(User(row[Long]("id"), row[String]("name"), row[Boolean]("admin_flag")))
          case None => None
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
        SQL("insert into Categories(name) values ({name})").on(
          'name -> category.name).executeInsert()
      category.id = id.get
    }
    category
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
      val selectCategory= SQL("Select * from Categories;")
      val categories = selectCategory().map(row => Category(row[Long]("id"), row[String]("name"))).toList
      categories
    }
  }

  def getCategory(id: Long): Option[Category] = {
    DB.withConnection { implicit c =>
      val selectCategory = SQL("Select * from Categories where id = {id} limit 1;").on('id -> id).apply
        .headOption
      selectCategory match {
        case Some(row) => Some(Category(row[Long]("id"), row[String]("name")))
        case None => None
      }
    }
  }

}

object UserDao extends UserDaoT
