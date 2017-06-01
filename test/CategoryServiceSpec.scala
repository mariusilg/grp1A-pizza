/**
  * Created by sabri on 10.05.2017.
  */
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test.Helpers._
import play.api.test._
import services._

@RunWith(classOf[JUnitRunner])
class CategoryServiceSpec extends Specification {

  def memDB[T](code: => T) =

    running(FakeApplication(additionalConfiguration = Map(
      "db.default.driver" -> "org.h2.Driver",
      "db.default.url" -> "jdbc:h2:mem:test;MODE=PostgreSQL"
    )))(code)

  def clearDatabase ()= {
    val availableCategories = CategoryService.availableCategories.length
    var nextID = 0
    do {
      CategoryService.rmCategory(nextID);
      nextID += 1
    } while (availableCategories >= nextID)
  }


  "The CategoryService" should {

    "return a empty list of categories first" in memDB {
      clearDatabase()
      CategoryService.availableCategories must beEmpty
    }

    "add a category Alkoholische Getränke" in memDB {
      CategoryService.addCategory("Alkoholische Getränke", "ml", true ).name must be equalTo("Alkoholische Getränke")
    }

    "add a category Kaffee und Kuchen" in memDB {
      CategoryService.addCategory("Kaffee und Kuchen", "pro Stück", true ).name must be equalTo("Kaffee und Kuchen")
    }

    "add a category Extras" in memDB {
      CategoryService.addCategory("Extras", "pro Stück", true ).name must be equalTo("Extras")
    }

    "return a list containing just Pizza after adding category Pizza" in memDB {
      clearDatabase()
      CategoryService.addCategory("Alkoholische Getränke", "ml", true)
      val availableCategories = CategoryService.availableCategories
      CategoryService.availableCategories.length must be equalTo(1)
      availableCategories(0).name must be equalTo("Alkoholische Getränke")
    }

    "return a list of two categories after adding two categories" in memDB {
      clearDatabase()
      CategoryService.addCategory("Alkoholische Getränke", "ml", true)
      CategoryService.addCategory("Kaffee und Kuchen", "pro Stück", true )
      CategoryService.availableCategories.length must be equalTo(2)
    }

    "return a list of three categories after adding three categories" in memDB {
      clearDatabase()
      CategoryService.addCategory("Alkoholische Getränke", "ml", true)
      CategoryService.addCategory("Kaffee und Kuchen", "pro Stück", true )
      CategoryService.addCategory("Extras", "pro Stück", true )
      CategoryService.availableCategories.length must be equalTo(3)
    }

    "update category Alkoholische Getränke" in memDB {
      clearDatabase()
      CategoryService.addCategory("Alkoholische Getränke", "cm", true)
      CategoryService.updateCategory(4, "Salate", "cm", true)
      val updatedCategory = CategoryService.availableCategories
      CategoryService.availableCategories.length must be equalTo(1)
      updatedCategory(0).name must be equalTo("Salate")
    }


    "remove category Extras" in memDB {
      clearDatabase()
      CategoryService.addCategory("Pizza", "cm", true)
      val deletedCategory = CategoryService.rmCategory(4)
      CategoryService.availableCategories.length must be equalTo(0)
      deletedCategory must beTrue
    }

    "return a list with all visible categories" in memDB {
      clearDatabase()
      CategoryService.addCategory("Pizza", "cm", true)
      CategoryService.addCategory("Getränke", "ml", false)
      CategoryService.addCategory("Extras", "pro Stück", true)
      CategoryService.visibleCategories.length  must be equalTo(2)
    }

    "return the default sizes of a category by id" in memDB {
      CategoryService.getDefaultSize(1) must beBetween(9,31)
      CategoryService.getDefaultSize(2) must beBetween(250,750)
      CategoryService.getDefaultSize(3) must beBetween(49,101)
    }


    "return the sizes of a category" in memDB {
      CategoryService.getSizes(1).length must be equalTo(3)
      CategoryService.getSizes(2).length must be equalTo(3)
      CategoryService.getSizes(3).length must be equalTo(2)
    }

    "return wheter a category has specific sizes or not" in memDB {
      clearDatabase()
      CategoryService.addCategory("Pizza", "cm", true)
      CategoryService.hasSizes(4) must beFalse
      CategoryService.hasSizes(1) must beTrue
    }

    "return the unit name of a category" in memDB {
      CategoryService.getUnit(1) must be equalTo("cm")
      CategoryService.getUnit(2) must be equalTo("ml")
      CategoryService.getUnit(3) must be equalTo("g")
    }

    "return that there is one visible category left" in memDB {
      CategoryService.deactivateCategory(1) must beTrue
      CategoryService.deactivateCategory(2) must beTrue
      CategoryService.lastVisibleCategory(1) must beFalse
      CategoryService.lastVisibleCategory(2) must beFalse
      CategoryService.lastVisibleCategory(3) must beTrue
    }

    /*ToDo: mit Nils absprechen, ob richtig getestet*/
    "return that there is no visible category left" in memDB {
      CategoryService.deactivateCategory(1) must beTrue
      CategoryService.deactivateCategory(2) must beTrue
      CategoryService.deactivateCategory(3) must beTrue
      CategoryService.lastVisibleCategory(1) must beTrue
      CategoryService.lastVisibleCategory(2) must beTrue
      CategoryService.lastVisibleCategory(3) must beTrue
    }

    /*ToDo: mit Nils absprechen, ob richtig getestet*/
    "Return whether new name of category exists or not" in memDB {
      clearDatabase()
      CategoryService.addCategory("Pizza", "cm", true)
      CategoryService.nameInUse(4,"Pizza") must beFalse
      CategoryService.nameInUse(5,"Extras") must beFalse
    }

    "Return a specific Category" in memDB {
      CategoryService.getCategory(1) must beSome
      CategoryService.getCategory(4) must beNone
    }

    "Return the first visible category id" in memDB {
      CategoryService.getDefaultCategory must beSome
      CategoryService.deactivateCategory(1) must beTrue
      CategoryService.deactivateCategory(2) must beTrue
      CategoryService.deactivateCategory(3) must beTrue
      CategoryService.getDefaultCategory must beNone
    }

    "Returns whether a category is visible or not" in memDB {
      clearDatabase()
      CategoryService.addCategory("Pizza", "cm", true)
      CategoryService.isCategoryVisible(4) must beTrue
      CategoryService.deactivateCategory(4) must beTrue
      CategoryService.isCategoryVisible(4) must beFalse
    }

    /*ToDo: noch testen, dass False rauskommt, wenn über diese Kategorie
    schon mal was bestellt wurde
     */
    "Returns whether a category is deletable or not" in memDB {
      CategoryService.isCategoryDeletable(1) must beTrue
    }







  }
}