package models

/**
 * User entity.
 * @param id database id of the user.
 * @param userName username of the user.
 * @param firstName firstname of the user.
 * @param lastName lastname of the user.
 * @param password password of the user.
 * @param admin optional admin-flag of user.
 * @param street adress of user.
 * @param zip zip-code of user.
 * @param city city of user.
 * @param phone phone number of user.
 * @param email email address of user.
 * @param distance distance of user to the shop.
 * @param active optional active-flag of the user.
 */
case class User(var id: Long, var userName: String, var firstName: String, var lastName: String, var password: String, var admin: Boolean, var street: String, var zip: String, var city: String, var phone: String, var email: String, var distance: Int, var active: Boolean)