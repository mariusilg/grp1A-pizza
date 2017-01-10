package forms

/**
  * Form containing data to create a new category.
  * @param name name of the category.
  */
case class CreateCategoryForm(id: Option[Long], name: String, unit: String, visibility: Option[Boolean])