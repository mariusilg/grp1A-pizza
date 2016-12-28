package forms

/**
 * Form containing data to create a user.
 * @param name name of the user.
 * @param password password of the user.
 * @param admin adminflag of user.
 * @param distance distance of user to the shop.
 */
case class CreateUserForm(name: String, password: String, admin: Option[Boolean], distance: Int)