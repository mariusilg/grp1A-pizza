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
        SQL("insert into Users(name, password, admin_flag, distance) values ({name}, {password}, {admin}, {distance})").on(
          'name -> user.name, 'password -> user.password, 'admin -> user.admin, 'distance -> user.distance).executeInsert()
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
      val selectUsers = SQL("Select id, name, admin_flag, distance from Users;")
      // Transform the resulting Stream[Row] to a List[(String,String)]
      val users = selectUsers().map(row => User(row[Long]("id"), row[String]("name"), null, row[Boolean]("admin_flag"), row[Int]("distance"))).toList
      users
    }
  }

  /**
    * Returns a list of available customers from the database.
    * @return a list of user objects.
    */
  def registeredCustomers: List[User] = {
    DB.withConnection { implicit c =>
      val selectCustomers = SQL("Select id, name, admin_flag, distance from Users where admin_flag = false;")
      // Transform the resulting Stream[Row] to a List[(String,String)]
      val customers = selectCustomers().map(row => User(row[Long]("id"), row[String]("name"), null, row[Boolean]("admin_flag"), row[Int]("distance"))).toList
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
          case Some(row) => Some(User(row[Long]("id"), row[String]("name"), row[String]("password"), row[Boolean]("admin_flag"), row[Int]("distance")))
          case None => None
        }
      }
    }


  /**
    * Updates a user from the database.
    * @return whether update was successful or not.
    */
  def updateUser(user: User): Boolean = {
    DB.withConnection { implicit c =>
      val rowsUpdated = SQL("update Users SET name={name}, password={password}, admin_flag={admin}, distance={distance} where id = {id}").on('name -> user.name, 'password -> user.password, 'admin -> user.admin, 'distance -> user.distance, 'id -> user.id).executeUpdate()
      rowsUpdated == 1
    }
  }

  /**
    * Returns a user from the database.
    * @return user.
    */
  def getUserByID(id: Long): Option[User] = {
    DB.withConnection { implicit c =>
      val selectUser = SQL("Select * from Users where id = {id};").on('id -> id).apply
        .headOption
      selectUser match {
        case Some(row) => Some(User(row[Long]("id"), row[String]("name"), row[String]("password"), row[Boolean]("admin_flag"), row[Int]("distance")))
        case None => None
      }
    }
  }

  /**
    * Returns a user from the database.
    * @return user.
    */
  def login(name: String, password: String): Option[Long] = {
    DB.withConnection { implicit c =>
      val checkUser = SQL("Select id from Users where name = {name} and password = {password} limit 1;").on('name -> name, 'password -> password).apply
        .headOption
      checkUser match {
        case Some(row) => Some(row[Long]("id"))
        case None => None
      }
    }
  }

  /**
    * Returns a user from the database.
    * @return user.
    */
  def checkName(name: String): Boolean = {
    DB.withConnection { implicit c =>
      val checkName = SQL("Select COUNT(*) as cnt from Users where UPPER(name) = UPPER({name});").on('name -> name).apply
        .headOption
      checkName match {
        case Some(row) => row[Long]("cnt") == 1
        case None => false
      }
    }
  }

}

object UserDao extends UserDaoT
