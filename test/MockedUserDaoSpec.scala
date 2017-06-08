import org.junit.runner.RunWith
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

@RunWith(classOf[JUnitRunner])
class MockedUserDaoSpec extends Specification with Mockito {

  "The UserService" should {
    "return a list of three users" in {
      UserService.registeredUsers.length must be equalTo(3)
    }
  }

  object UserService extends services.UserServiceT {
    override val userDao = mock[dbaccess.UserDaoT]
    import models.User
    userDao.registeredUsers returns List(User(1,"Helge", true, "Helge", "Schneider", "pw", false, "street", "plz", "city","0176xxxxx", "", -1, true)
      , User(2,"Helga", true, "Helga", "Schneider", "pw", false, "street", "plz", "city","0176xxxxx", "", -1, true)
      , User(3,"Tina", true, "Tina", "Schneider", "pw", false, "street", "plz", "city","0176xxxxx", "", -1, true)
    )
  }

}
