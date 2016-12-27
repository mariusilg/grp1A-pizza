package forms

/**
  * Form containing data to update a user.
  * @param id id of user.
  * @param name name of the user.
  * @param password password of the user.
  * @param admin adminflag of user.
  */
case class EditUserForm(id: Long, name: String /*, password: String*/, admin: Boolean)