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
  def addUser(user: User, token: String): User = {
    DB.withConnection { implicit c =>
      val id: Option[Long] =
        SQL("insert into Users(username, gender, first_name, last_name, password, admin_flag, street, zip, city, phone, email, distance, active_flag, token)" +
          " values ({username}, {gender}, {first_name}, {last_name}, {password}, {admin}, {street}, {zip}, {city}, {phone}, {email}, {distance}, {active}, {token})").on(
          'username -> user.userName, 'gender -> user.gender, 'first_name -> user.firstName, 'last_name -> user.lastName, 'password -> user.password,
          'admin -> user.admin, 'street -> user.street, 'zip -> user.zip, 'city -> user.city, 'phone -> user.phone, 'email -> user.email,
          'distance -> user.distance, 'active -> user.active, 'token -> token).executeInsert()
      user.id = id.get
    }
    user
  }

  /**
    * Updates the user's distance in the database.
    * @param id id of user.
    * @param distance distance of user to the shop.
    * @return whether update was successful or not
    */
  def updateDistance(id: Long, distance: Int): Boolean = {
    DB.withConnection { implicit c =>
      val rowsUpdated = SQL("update Users SET distance = {distance} where id = {id}").on('distance -> distance, 'id -> id).executeUpdate()
      rowsUpdated == 1
    }
  }

  def confirmAccount(id: Long, token: String): Boolean = {
    DB.withConnection { implicit c =>
      val rowsUpdated = SQL("update Users SET active_flag = TRUE where id = {id} and token = {token}").on('id -> id, 'token -> token).executeUpdate()
      rowsUpdated == 1
    }
  }

  /**
   * Removes a user by id from the database.
   * @param id the users id.
   * @return a boolean success flag
   */
  def rmUser(id: Long): Boolean = {
    DB.withConnection { implicit c =>
      val rowsCount = SQL("delete from Users where id = {id}").on('id -> id).executeUpdate()
      rowsCount > 0
    }
  }

  /**
   * Returns a list of registered users from the database.
   * @return a list of user objects.
   */
  def registeredUsers: List[User] = {
    DB.withConnection { implicit c =>
      val selectUsers = SQL("Select id, username, gender, first_name, last_name, admin_flag, street, zip, city, phone, email, distance, active_flag from Users;")
      val users = selectUsers().map(row => User(row[Long]("id"), row[String]("username"), row[Boolean]("gender"), row[String]("first_name"), row[String]("last_name"), null, row[Boolean]("admin_flag"), row[String]("street"), row[String]("zip"), row[String]("city"), row[String]("phone"), row[String]("email"), row[Int]("distance"), row[Boolean]("active_flag"))).toList
      users
    }
  }

  /**
    * Returns a list of registered customers from the database.
    * @return a list of user objects.
    */
  def registeredCustomers: List[User] = {
    DB.withConnection { implicit c =>
      val selectCustomers = SQL("Select id, username, gender, first_name, last_name, street, zip, city, phone, email, distance, active_flag from Users where admin_flag = false;")
      val customers = selectCustomers().map(row => User(row[Long]("id"), row[String]("username"), row[Boolean]("gender"), row[String]("first_name"), row[String]("last_name"),  null, false, row[String]("street"), row[String]("zip"), row[String]("city"), row[String]("phone"), row[String]("email"), row[Int]("distance"), row[Boolean]("active_flag"))).toList
      customers
    }
  }

  /**
    * Returns a user from the database.
    * @param userName username of the user.
    * @return optional user
    */
  def getUser(userName: String): Option[User] = {
    DB.withConnection { implicit c =>
        val selectUser = SQL("Select id, username, gender, first_name, last_name, password, admin_flag, street, zip, city, phone, email, distance, active_flag from Users where LOWER(username) = {username} limit 1;").on('username -> userName.toLowerCase).apply
          .headOption
        selectUser match {
          case Some(row) => Some(User(row[Long]("id"), row[String]("username"), row[Boolean]("gender"), row[String]("first_name"), row[String]("last_name"), row[String]("password"), row[Boolean]("admin_flag"), row[String]("street"), row[String]("zip"), row[String]("city"), row[String]("phone"), row[String]("email"), row[Int]("distance"), row[Boolean]("active_flag")))
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
      val rowsUpdated = SQL("update Users SET username={username}, gender={gender}, first_name={firstName}, last_name={lastName}, password={password}" +
        " ,admin_flag={admin}, street={street}, zip={zip}, city={city}, phone={phone}, email={email}, active_flag={active} where id = {id}").on(
        'username -> user.userName,
        'gender -> user.gender,
        'firstName ->user.firstName,
        'lastName -> user.lastName,
        'password -> user.password,
        'admin -> user.admin,
        'street -> user.street
        , 'zip -> user.zip,
        'city -> user.city,
        'phone -> user.phone,
        'email -> user.email,
        'active -> user.active,
        'id -> user.id).executeUpdate()
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
      val selectUser = SQL("Select id, username, gender, first_name, last_name, password, admin_flag, street, zip, city, phone, email, distance, active_flag from Users where id = {id};").on('id -> id).apply
        .headOption
      selectUser match {
        case Some(row) => Some(User(row[Long]("id"), row[String]("username"), row[Boolean]("gender"), row[String]("first_name"), row[String]("last_name"), row[String]("password"), row[Boolean]("admin_flag"), row[String]("street"), row[String]("zip"), row[String]("city"), row[String]("phone"), row[String]("email"), row[Int]("distance"), row[Boolean]("active_flag")))
        case None => None
      }
    }
  }

  /**
    * Returns the users id by name and password from the database.
    * @param userName userName of the user.
    * @param password the users password.
    * @return optional user
    */
  def login(userName: String, password: String): Option[User] = {
    DB.withConnection { implicit c =>
      val checkUser = SQL("Select id from Users where LOWER(username) = {username} and password = {password} limit 1;").on('username -> userName.toLowerCase, 'password -> password).apply
        .headOption
      checkUser match {
        case Some(row) => getUserByID(row[Long]("id"))
        case None => None
      }
    }
  }


  /**
    * Returns the users id by name and password from the database.
    * @param userID id of the user.
    * @return optional token
    */
  def getTokenByUserID(userID: Long): Option[String] = {
    DB.withConnection { implicit c =>
      val getToken = SQL("Select token from Users where id = {id}").on('id -> userID).apply
        .headOption
      getToken match {
        case Some(row) => Some(row[String]("token"))
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
    * @param userName a username.
    * @return true (inUse) / false
    */
  def nameInUse(userName: String): Boolean = {
    DB.withConnection { implicit c =>
      val checkName = SQL("Select COUNT(*) as cnt from Users where UPPER(username) = UPPER({username});").on('username -> userName).apply
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
    * @param  userName username.
    * @return Boolean
    */
  def nameInUse(id: Long, userName: String): Boolean = {
    DB.withConnection { implicit c =>
      val checkAvailability = SQL("Select COUNT(*) as cnt from Users where UPPER(username) = UPPER({username}) and id <> {id};").on('username -> userName, 'id -> id).apply
        .headOption
      checkAvailability match {
        case Some(row) => row[Long]("cnt") != 0
        case None => true
      }
    }
  }

  /**
    * Checks whether eMail address is in use or not in the database.
    * @param eMail eMail address of user.
    * @return true (inUse) / false
    */
  def eMailInUse(eMail: String): Boolean = {
    DB.withConnection { implicit c =>
      val checkName = SQL("Select COUNT(*) as cnt from Users where UPPER(email) = UPPER({email});").on('email -> eMail).apply
        .headOption
      checkName match {
        case Some(row) => row[Long]("cnt") > 0
        case None => true
      }
    }
  }

  /**
    * Returns whether eMail address already in use or not.
    * @param id the users id.
    * @param  eMail new eMail address of user.
    * @return Boolean
    */
  def eMailInUse(id: Long, eMail: String): Boolean = {
    DB.withConnection { implicit c =>
      val checkAvailability = SQL("Select COUNT(*) as cnt from Users where UPPER(email) = UPPER({email}) and id <> {id};").on('email -> eMail, 'id -> id).apply
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
      val cntUserOrders = SQL("Select COUNT(*) as cnt from Orders where cust_id = {id} and state <> 'inCart';").on('id -> id).apply
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
      val activeCustCount = SQL("Select COUNT(*) as cnt from Users where admin_flag = 0 and active_flag = TRUE").apply
        .headOption
      activeCustCount match {
        case Some(row) => row[Long]("cnt")
        case None => 0
      }
    }
  }

}

object UserDao extends UserDaoT
