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
  def addUser(name: String): User = {
    // create User
    val newUser = User(-1, name, false)
    // persist and return User
    userDao.addUser(newUser)
  }

  /**
   * Removes a user by id from the system.
   * @param id users id.
   * @return a boolean success flag.
   */
  def rmUser(id: Long): Boolean = userDao.rmUser(id)

  /**
   * Gets a list of all registered users.
   * @return list of users.
   */
  def registeredUsers: List[User] = {
    userDao.registeredUsers
  }

  /**
    * Return whether user is admin or not.
    * @return whether user is admin or not.
    */
  def getUser(name: String): Option[User] = {
    userDao.getUser(name)
  }



  /**
    * Adds a new category to the system.
    * @param name name of the new category.
    * @return the new category.
    */
  def addCategory(name: String): Category = {
    // create Category
    val newCategory = Category(-1, name)
    // persist and return User
    userDao.addCategory(newCategory)
  }

  /**
    * Removes a category by id from the system.
    * @param id id of category.
    * @return a boolean success flag.
    */
  def rmCategory(id: Long): Boolean = userDao.rmCategory(id)

  /**
    * Gets a list of all available categories.
    * @return list of categories.
    */
  def availableCategories: List[Category] = {
    userDao.availableCategories
  }

  def getCategory(id: Long): Option[Category] = {
    userDao.getCategory(id)
  }


  def getItemsByCategory(id: Long): List[Item] = {
    userDao.getItemsByCategory(id)
  }

}

object UserService extends UserServiceT
