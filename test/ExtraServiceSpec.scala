/**
  * Created by sabri on 21.05.2017.
  */
import org.junit.runner._
import org.specs2.mutable._
import org.specs2.runner._
import play.api.test.Helpers._
import play.api.test._
import services._

@RunWith(classOf[JUnitRunner])
class ExtraServiceSpec extends Specification {

  def memDB[T](code: => T) =
    running(FakeApplication(additionalConfiguration = Map(
      "db.default.driver" -> "org.h2.Driver",
      "db.default.url" -> "jdbc:h2:mem:test;MODE=PostgreSQL"
    )))(code)

  def clearDatabase() = {
    val availableExtras = ExtraService.getExtras.length
    var nextID = 0
    do {
      CategoryService.rmCategory(nextID);
      nextID += 1
    } while (availableExtras >= nextID)
  }


  "The ExtraService" should {

    "return a list of extras" in memDB {
      ExtraService.getExtras.length must be equalTo(8)
    }

    "return an extra by id" in memDB {
      ExtraService.getExtra(1) must beSome
      ExtraService.getExtra(2) must beSome
      ExtraService.getExtra(3) must beSome
      ExtraService.getExtra(4) must beSome
      ExtraService.getExtra(5) must beSome
      ExtraService.getExtra(6) must beSome
      ExtraService.getExtra(7) must beSome
      ExtraService.getExtra(8) must beSome
      ExtraService.getExtra(9) must beNone
    }
  }

}