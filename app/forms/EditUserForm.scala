package forms

/**
  * Form containing data to update a user.
  * @param id id of user.
  * @param name name of the user.
  * @param password password of the user.
  * @param admin admin-flag of user.
  * @param distance distance of user to the shop.
  * @param active active-flag of the user.
  */
case class EditUserForm(id: Long, name: String, password: String, admin: Boolean, distance: Int, active: Boolean)