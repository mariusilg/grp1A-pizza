package forms

/**
 * Form containing data to create a user.
 * @param name name of the user.
 * @param password password of the user.
 * @param admin adminflag of user.
 */
case class CreateUserForm(name: String, password: String, admin: Option[Boolean])