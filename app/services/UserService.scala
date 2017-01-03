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
   * @param name name of the new user.
   * @return the new user.
   */
  def addUser(name: String, password: String, admin: Boolean, distance: Int): User = {
    // create User
    val newUser = User(-1, name, password, admin, distance)
    // persist and return User
    userDao.addUser(newUser)
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
    * Return id of user if he exists.
    * @return id of the user.
    */
  def login(name: String, password: String): Option[Long] = {
    userDao.login(name, password)
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
  def nameInUse(id: Long, name: String): Boolean = {
    userDao.nameInUse(id, name)
  }

  /**
    * Return whether username exists or not.
    * @return true or false.
    */
  def updateUser(user: User): Unit = {
    userDao.updateUser(user)
  }

  /**
    * Return whether there is only one admin left.
    * @return boolean.
    */
  def lastAdmin : Boolean = userDao.lastAdmin

  /**
    * Return whether user is admin or not.
    * @return boolean.
    */
  def userIsAdmin(id: Long) : Boolean = userDao.userIsAdmin(id)

    /**
    * Checks whether a user is deletable from the system.
    * @param id users id.
    * @return a boolean success flag.
    */
  def userIsDeletable(id: Long): Boolean = userDao.userIsDeletable(id)

  /**
    * Removes a user by id from the system.
    * @param id users id.
    * @return a boolean success flag.
    */
  def rmUser(id: Long): Boolean = userDao.rmUser(id)

  /**
    * Customer count.
    */
  def getCustCount : Long = userDao.getCustCount
}

object UserService extends UserServiceT
