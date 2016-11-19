package dbaccess

import anorm.SQL
import play.api.Play.current
import play.api.db.DB
import anorm.NamedParameter.symbol
import models._

/**
 * Data access object for user related operations.
 *
 * @author ob, scs
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
    * Returns a admin_flag of specific user from the database.
    * @return whether user is admin or not.
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


  /**
    * Returns a list of available items by category from the database.
    * @return a list of item objects.
    */
  def getItemsByCategory(id: Long): List[Item] = {
    DB.withConnection { implicit c =>
      val selectItemsByCategory= SQL("Select id, name from Items where cat_id = {id};").on('id -> id)
      val itemsByCategory = selectItemsByCategory().map(row => Item(row[Long]("id"), row[String]("name"))).toList
      itemsByCategory
    }
  }



  def addOrder(order: Order): Order = {
    DB.withConnection { implicit c =>
      val id: Option[Long] =
        SQL("insert into Orders(cust_id, item_id, quantity, costs) values ({custID}, {itemID}, {quantity}, {costs})").on(
          'custID -> order.custID, 'itemID -> order.itemID, 'quantity -> order.quantity, 'costs -> order.costs).executeInsert()
      //order.id = 1 //id.get
    }
    order
  }

}

object UserDao extends UserDaoT
