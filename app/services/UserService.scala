package services

import dbaccess.{UserDao, UserDaoT}
import models._

/**
 * Service class for user related operations.
 *
 * @author ob, scs
 */
trait UserServiceT {

  val userDao: UserDaoT = UserDao

  /**
   * Adds a new user to the system.
   * @param userName userName of the new user.
   * @return the new user.
   */
  def addUser(userName: String, firstName: String, lastName: String, password: String, admin: Boolean, street: String, zip: String, city: String, phone: String, email: String, active: Boolean, token: String): User = {
    // create User
    val newUser = User(-1, userName, firstName, lastName, password, admin, street, zip, city, phone, email, -1, active)
    // persist and return User
    val user = userDao.addUser(newUser, token)
    controllers.WSController.updateDistance(user)
    if(!active) {
      controllers.MailController.confirmMail(user, token)
    }
    user
  }

  def updateDistance(id: Long, distance: Int): Boolean= {
    userDao.updateDistance(id, distance)
  }

  def confirmAccount(id: Long, token: String): Boolean= {
    userDao.confirmAccount(id, token)
  }

  def getTokenByUserID(id: Long): Option[String] = {
    userDao.getTokenByUserID(id)
  }

  /**
   * Gets a list of all registered users.
   * @return list of users.
   */
  def registeredUsers: List[User] = {
    userDao.registeredUsers
  }

  /**
    * Gets a list of all registered customers.
    * @return list of customers.
    */
  def registeredCustomers: List[User] = {
    userDao.registeredCustomers
  }

  /**
    * Return whether user is admin or not.
    * @return whether user is admin or not.
    */
  def getUser(name: String): Option[User] = {
    userDao.getUser(name)
  }



  /**
    * Return user if he exists.
    * @return optional user object.
    */
  def getUserByID(id: Long): Option[User] = {
    userDao.getUserByID(id)
  }

  /**
    * Return id of user if the user exists.
    * @param userName username of the user.
    * @param password password of the user.
    * @return optional user.
    */
  def login(userName: String, password: String): Option[User] = {
    userDao.login(userName, password)
  }

  /**
    * Return whether username exists or not.
    * @return true or false.
    */
  def nameInUse(name: String): Boolean = {
    userDao.nameInUse(name)
  }

  /**
    * Return whether new username exists or not.
    * @return true or false.
    */
  def nameInUse(id: Long, name: String): Boolean = userDao.nameInUse(id, name)

  /**
    * Return whether username exists or not.
    * @return true or false.
    */
  def updateUser(user: User): Unit = {
    userDao.updateUser(user)
    controllers.WSController.updateDistance(user)
  }

  /**
    * Return whether the user is the last admin or not.
    * @return boolean.
    */
  def lastAdmin(id: Long) : Boolean = userDao.lastAdmin(id)

  /**
    * Return whether user is admin or not.
    * @return boolean.
    */
  def userIsAdmin(id: Long) : Boolean = userDao.userIsAdmin(id)


  /**
    * Return whether user is activated / deactivated.
    * @return true (activated) / false (deactivated).
    */
  def userIsActive(id: Long) : Boolean = userDao.userIsActive(id)

    /**
    * Checks whether a user is deletable from the system.
    * @param id users id.
    * @return a boolean success flag.
    */
  def userIsDeletable(id: Long): Boolean = userDao.userIsDeletable(id)

  /**
    * Deactivates a user by id from the system.
    * @param id users id.
    * @return a boolean success flag.
    */
  def deactivateUser(id: Long): Boolean = userDao.deactivateUser(id)


  /**
    * Removes a user by id from the system.
    * @param id users id.
    * @return a boolean success flag.
    */
  def rmUser(id: Long): Boolean = userDao.rmUser(id)


  /*##########################################################################
  ##                          REPORTING                                     ##
  ##########################################################################*/

  /**
    * Customer count.
    */
  def getCustCount : Long = userDao.getCustCount

  /**
    * Active Customer count.
    */
  def getActiveCustCount : Long = userDao.getActiveCustCount
}

object UserService extends UserServiceT
