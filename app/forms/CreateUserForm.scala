package forms

/**
 * Form containing data to create a user.
 * @param name name of the user.
 * @param password password of the user.
 * @param admin optional adminflag of user.
 * @param distance distance of user to the shop.
 * @param active optional active_flag of the user.
 */
case class CreateUserForm(name: String, password: String, admin: Option[Boolean], distance: Int, active: Option[Boolean])