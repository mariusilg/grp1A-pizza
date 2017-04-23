package forms

/**
 * Form containing data to create a user.
 * @param userName username of the user.
 * @param gender gender of the user.
 * @param firstName firstname of the user.
 * @param lastName lastname of the user.
 * @param password password of the user.
 * @param admin optional admin-flag of user.
 * @param street adress of user.
 * @param zip zip-code of user.
 * @param city city of user.
 * @param phone phone number of user.
 * @param email email address of user.
 * @param active optional active-flag of the user.
 */
case class CreateUserForm(userName: String, gender: String, firstName: String, lastName: String, password: String, admin: Option[Boolean], street: String, zip: String, city: String, phone: String, email: String, active: Option[Boolean])