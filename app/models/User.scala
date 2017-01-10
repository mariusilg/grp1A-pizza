package models

/**
 * User entity.
 * @param id database id of the user.
 * @param name name of the user.
 * @param password password of the user.
 * @param admin admin_flag of the user.
 * @param distance distance of the user to the shop.
 * @param active active_flag of the user.
 */
case class User(var id: Long, var name: String, var password: String, var admin: Boolean, var distance: Int, var active: Boolean)
