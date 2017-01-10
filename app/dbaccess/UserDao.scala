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
        SQL("insert into Users(name, password, admin_flag, distance, active_flag) values ({name}, {password}, {admin}, {distance}, {active})").on(
          'name -> user.name, 'password -> user.password, 'admin -> user.admin, 'distance -> user.distance, 'active -> user.active).executeInsert()
      user.id = id.get
    }
    user
  }

  /**
   * Removes a user by id from the database.
   * @param id the users id.
   * @return a boolean success flag
   */
  def rmUser(id: Long): Boolean = {
    DB.withConnection { implicit c =>
      val rowsCount = SQL("delete from Users where id = ({id})").on('id -> id).executeUpdate()
      rowsCount > 0
    }
  }

  /**
   * Returns a list of registered users from the database.
   * @return a list of user objects.
   */
  def registeredUsers: List[User] = {
    DB.withConnection { implicit c =>
      val selectUsers = SQL("Select id, name, admin_flag, distance, active_flag from Users;")
      val users = selectUsers().map(row => User(row[Long]("id"), row[String]("name"), null, row[Boolean]("admin_flag"), row[Int]("distance"), row[Boolean]("active_flag"))).toList
      users
    }
  }

  /**
    * Returns a list of registered customers from the database.
    * @return a list of user objects.
    */
  def registeredCustomers: List[User] = {
    DB.withConnection { implicit c =>
      val selectCustomers = SQL("Select id, name, admin_flag, distance, active_flag from Users where admin_flag = false;")
      val customers = selectCustomers().map(row => User(row[Long]("id"), row[String]("name"), null, row[Boolean]("admin_flag"), row[Int]("distance"), row[Boolean]("active_flag"))).toList
      customers
    }
  }

  /**
    * Returns a user from the database.
    * @param name the users name.
    * @return optional user
    */
  def getUser(name: String): Option[User] = {
    DB.withConnection { implicit c =>
        val selectUser = SQL("Select * from Users where name = {name} limit 1;").on('name -> name).apply
          .headOption
        selectUser match {
          case Some(row) => Some(User(row[Long]("id"), row[String]("name"), row[String]("password"), row[Boolean]("admin_flag"), row[Int]("distance"), row[Boolean]("active_flag")))
          case None => None
        }
      }
    }


  /**
    * Updates a user from the database.
    * @param user user object.
    * @return whether update was successful or not
    */
  def updateUser(user: User): Boolean = {
    DB.withConnection { implicit c =>
      val rowsUpdated = SQL("update Users SET name={name}, password={password}, admin_flag={admin}, distance={distance}, active_flag={active} where id = {id}").on('name -> user.name, 'password -> user.password, 'admin -> user.admin, 'distance -> user.distance, 'active -> user.active, 'id -> user.id).executeUpdate()
      rowsUpdated == 1
    }
  }

  /**
    * Deactivates a user by id from the database.
    * @param id the users id.
    * @return whether update was successful or not
    */
  def deactivateUser(id: Long): Boolean = {
    DB.withConnection { implicit c =>
      val rowsUpdated = SQL("update Users SET active_flag = FALSE where id = {id}").on('id -> id).executeUpdate()
      rowsUpdated == 1
    }
  }

  /**
    * Returns a user by id from the database.
    * @param id the users id.
    * @return optional user object
    */
  def getUserByID(id: Long): Option[User] = {
    DB.withConnection { implicit c =>
      val selectUser = SQL("Select * from Users where id = {id};").on('id -> id).apply
        .headOption
      selectUser match {
        case Some(row) => Some(User(row[Long]("id"), row[String]("name"), row[String]("password"), row[Boolean]("admin_flag"), row[Int]("distance"), row[Boolean]("active_flag")))
        case None => None
      }
    }
  }

  /**
    * Returns the users id by name and password from the database.
    * @param name the users name.
    * @param password the users password.
    * @return optional user id
    */
  def login(name: String, password: String): Option[Long] = {
    DB.withConnection { implicit c =>
      val checkUser = SQL("Select id from Users where UPPER(name)=UPPER({name}) and password = {password} limit 1;").on('name -> name, 'password -> password).apply
        .headOption
      checkUser match {
        case Some(row) => Some(row[Long]("id"))
        case None => None
      }
    }
  }

  /**
    * Returns the active-flag of a specific user by id from the database.
    * @param id the users id.
    * @return boolean active-flag
    */
  def userIsActive(id: Long): Boolean = {
    DB.withConnection { implicit c =>
      val activeFlag = SQL("Select active_flag from Users where id = {id};").on('id -> id).apply
        .headOption
      activeFlag match {
        case Some(row) => row[Boolean]("active_flag")
        case None => false
      }
    }
  }

  /**
    * Checks whether name is in use or not in the database.
    * @param name a name.
    * @return true (inUse) / false
    */
  def nameInUse(name: String): Boolean = {
    DB.withConnection { implicit c =>
      val checkName = SQL("Select COUNT(*) as cnt from Users where UPPER(name) = UPPER({name});").on('name -> name).apply
        .headOption
      checkName match {
        case Some(row) => row[Long]("cnt") > 0
        case None => true
      }
    }
  }

  /**
    * Returns whether username already in use or not.
    * @param id the users id.
    * @param  name name.
    * @return Boolean
    */
  def nameInUse(id: Long, name: String): Boolean = {
    DB.withConnection { implicit c =>
      val checkAvailability = SQL("Select COUNT(*) as cnt from Users where UPPER(name) = UPPER({name}) and id <> {id};").on('name -> name, 'id -> id).apply
        .headOption
      checkAvailability match {
        case Some(row) => row[Long]("cnt") != 0
        case None => true
      }
    }
  }

  /**
    * Returns whether user is the last admin or not.
    * @param id the users id.
    * @return Boolean
    */
  def lastAdmin(id: Long): Boolean = {
    DB.withConnection { implicit c =>
      val lastAdmin = SQL("Select COUNT(*) as cnt from Users where admin_flag = 1 and id <> {id};").on('id -> id).apply
        .headOption
      lastAdmin match {
        case Some(row) => row[Long]("cnt") == 0
        case None => true
      }
    }
  }

  /**
    * Returns if user has not ordered yet.
    * @return Boolean
    */
  def userIsDeletable(id: Long): Boolean = {
    DB.withConnection { implicit c =>
      val cntUserOrders = SQL("Select COUNT(*) as cnt from Orders where cust_id = {id};").on('id -> id).apply
        .headOption
      cntUserOrders match {
        case Some(row) => row[Long]("cnt") == 0
        case None => false
      }
    }
  }



  /**
    * Returns whether user is admin or not.
    * @param id the users id.
    * @return Boolean.
    */
  def userIsAdmin(id: Long): Boolean = {
    DB.withConnection { implicit c =>
      val adminCheck = SQL("Select admin_flag from Users where id = {id};").on('id -> id).apply
        .headOption
      adminCheck match {
        case Some(row) => row[Boolean]("admin_flag")
        case None => false
      }
    }
  }

  /**
    * Returns amount of customers registered in the system.
    * @return amount
    */
  def getCustCount: Long = {
    DB.withConnection { implicit c =>
      val custCount = SQL("Select COUNT(*) as  cnt from Users where admin_flag = 0").apply
        .headOption
      custCount match {
        case Some(row) => row[Long]("cnt")
        case None => 0
      }
    }
  }

  /**
    * Returns amount of customers registered in the system.
    * @return amount
    */
  def getActiveCustCount: Long = {
    DB.withConnection { implicit c =>
      val activeCustCount = SQL("Select COUNT(*) as  cnt from Users where admin_flag = 0 and active_flag = TRUE").apply
        .headOption
      activeCustCount match {
        case Some(row) => row[Long]("cnt")
        case None => 0
      }
    }
  }

}

object UserDao extends UserDaoT
