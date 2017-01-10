package forms

/**
  * Form containing data to create a new category.
  * @param id optional id of category.
  * @param name name of the category.
  * @param unit unit name.
  * @param visibility optional visibility-flag
  */
case class CreateCategoryForm(id: Option[Long], name: String, unit: String, visibility: Option[Boolean])