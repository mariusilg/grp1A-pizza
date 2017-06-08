/**
  * Created by sabri on 10.05.2017.
  */

import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._
import play.api.test._
import play.api.test.Helpers._
import services._

@RunWith(classOf[JUnitRunner])
class OrderServiceSpec extends Specification {

  def memDB[T](code: => T) =
    running(FakeApplication(additionalConfiguration = Map(
      "db.default.driver" -> "org.h2.Driver",
      "db.default.url" -> "jdbc:h2:mem:test;MODE=PostgreSQL"
    )))(code)

  def clearDatabase ()= {
    val orders = OrderService.getAllOrders.length
    var nextID = 0
    do {
      OrderService.cancelOrder(nextID);
      nextID += 1
    } while (orders >= nextID)
  }

  "The OrderService" should {

    "return an empty list of orders first" in memDB {
      clearDatabase()
      OrderService.getAllOrders must beEmpty
    }

    "add an order to Cart" in memDB {
      clearDatabase()
      OrderService.addToCart(1, 1, 5, 30, 10, List(1))
      OrderService.addToCart(1, 3, 6, 30, 10, List(3))
      OrderService.getCartByUserID(1).length must be equalTo(1)
      OrderService.addToCart(2, 1, 5, 30, 10, List(1))
      OrderService.getCartByUserID(2).length must be equalTo(1)
    }

    "return whether a cart was confirmed or not" in memDB {
      clearDatabase()
      OrderService.addToCart(1, 1, 5, 30, 10, List(1))
      OrderService.getCartByUserID(1).length must be equalTo(1)
      OrderService.confirmCart(1) must beTrue
    }

    "return the recent order by custID" in memDB {
      clearDatabase()
      OrderService.addToCart(1, 1, 5, 30, 20, List(1))
      OrderService.addToCart(1, 3, 6, 30, 10, List(3))
      OrderService.addToCart(2, 3, 6, 30, 15, List(3))
      OrderService.getCartByUserID(1).length must be equalTo(1)
      OrderService.getCartByUserID(2).length must be equalTo(1)
      OrderService.confirmCart(1) must beTrue
      OrderService.confirmCart(2) must beTrue
      OrderService.getRecentOrderByCustID(1).distance must be equalTo(20)
      OrderService.getRecentOrderByCustID(2).distance must be equalTo(15)
    }


    "Returns whether a cart is deletable or not" in memDB {
      clearDatabase()
      OrderService.addToCart(1, 1, 5, 30, 20, List(1))
      OrderService.addToCart(1, 3, 6, 30, 10, List(3))
      OrderService.deleteCart(1) must beTrue
    }


    "Returns whether a CartItem is deletable or not" in memDB {
      clearDatabase()
      OrderService.addToCart(1, 1, 5, 30, 20, List(1))
      OrderService.deleteCartItem(1,1) must beTrue
      OrderService.addToCart(2, 3, 6, 30, 15, List(3))
      OrderService.addToCart(2, 4, 5, 30, 15, List(3))
      OrderService.deleteCartItem(2,3) must beTrue
      OrderService.deleteCartItem(2,4) must beTrue

    }


    "Return whether a cartextra is deletable or not" in memDB {
      clearDatabase()
      OrderService.addToCart(2, 3, 6, 30, 15, List(3, 4, 5))
      OrderService.deleteCartExtra(2, 1) must beTrue
      OrderService.deleteCartExtra(2, 2) must beTrue
      OrderService.deleteCartExtra(2, 3) must beTrue
      OrderService.deleteCartExtra(2, 4) must beFalse
      OrderService.deleteCartExtra(2, 5) must beFalse

    }

    "Return, if an order can be cancelled by custID and orderID or not" in memDB {
      clearDatabase()
      OrderService.addToCart(2, 3, 6, 30, 15, List(3))
      OrderService.addToCart(2, 4, 5, 30, 15, List(4))
      OrderService.addToCart(1, 4, 5, 30, 15, List(4))
      OrderService.addToCart(1, 3, 6, 30, 15, List(3))
      OrderService.getCartByUserID(2).length must be equalTo(1)
      OrderService.getCartByUserID(1).length must be equalTo(1)
      OrderService.confirmCart(2) must beTrue
      OrderService.confirmCart(1) must beTrue
      OrderService.cancelOrder(2, 1) must beTrue
      OrderService.cancelOrder(2, 2) must beFalse
      OrderService.cancelOrder(1, 2) must beTrue
    }

    "Return, if an order can be cancelled by orderID or not" in memDB {
      clearDatabase()
      OrderService.addToCart(2, 3, 6, 30, 15, List(3))
      OrderService.addToCart(2, 4, 5, 30, 15, List(4))
      OrderService.addToCart(1, 4, 5, 30, 15, List(4))
      OrderService.addToCart(1, 3, 6, 30, 15, List(3))
      OrderService.getCartByUserID(2).length must be equalTo (1)
      OrderService.getCartByUserID(1).length must be equalTo (1)
      OrderService.confirmCart(2) must beTrue
      OrderService.confirmCart(1) must beTrue
      OrderService.cancelOrder(1) must beTrue
      OrderService.cancelOrder(2) must beTrue
      OrderService.cancelOrder(3) must beFalse
    }

    "Return whether an order can be accepted by orderID or not" in memDB {
      clearDatabase()
      OrderService.addToCart(2, 3, 6, 30, 15, List(3))
      OrderService.addToCart(2, 4, 5, 30, 15, List(4))
      OrderService.addToCart(1, 4, 5, 30, 15, List(4))
      OrderService.addToCart(1, 3, 6, 30, 15, List(3))
      OrderService.getCartByUserID(2).length must be equalTo (1)
      OrderService.getCartByUserID(1).length must be equalTo (1)
      OrderService.confirmCart(2) must beTrue
      OrderService.confirmCart(1) must beTrue
      OrderService.acceptOrder(1) must beTrue
      OrderService.acceptOrder(2) must beTrue
      OrderService.acceptOrder(3) must beFalse
    }

    "Return cartID by UserID" in memDB {
      clearDatabase()
      OrderService.addToCart(2, 3, 6, 30, 15, List(3))
      OrderService.addToCart(2, 4, 5, 30, 15, List(4))
      OrderService.addToCart(1, 4, 5, 30, 15, List(4))
      OrderService.addToCart(1, 3, 6, 30, 15, List(3))
      OrderService.getCartIDByUserID(1) must beSome
      OrderService.getCartIDByUserID(2) must beSome
      OrderService.getCartIDByUserID(3) must beNone
    }

    "Return cart by UserID" in memDB {
      clearDatabase()
      OrderService.addToCart(1, 4, 5, 30, 15, List(4))
      OrderService.addToCart(1, 3, 6, 30, 15, List(3))
      OrderService.addToCart(1, 3, 6, 30, 15, List(5))
      OrderService.addToCart(2, 3, 6, 30, 15, List(3))
      OrderService.addToCart(2, 4, 5, 30, 15, List(4))
      OrderService.addToCart(3, 4, 5, 30, 15, List(4))
      OrderService.addToCart(3, 3, 6, 30, 15, List(3))
      OrderService.addToCart(3, 3, 6, 30, 15, List(5))
      OrderService.addToCart(3, 3, 6, 30, 15, List(3))
      OrderService.getCartByUserID(1).length must be equalTo(1)
      OrderService.getCartByUserID(2).length must be equalTo(1)
      OrderService.getCartByUserID(3).length must be equalTo(1)
    }

    "Return the calculated product costs" in memDB {
      clearDatabase()
      OrderService.addToCart(1, 4, 5, 30, 15, List(4))
      OrderService.calcProductCost(5, 30, 5) must be equalTo(750)
    }

    "Return orders by CustID" in memDB {
      clearDatabase()
      OrderService.addToCart(1, 4, 5, 30, 15, List(4))
      OrderService.addToCart(1, 3, 6, 30, 15, List(3))
      OrderService.addToCart(1, 3, 6, 30, 15, List(5))
      OrderService.addToCart(2, 3, 6, 30, 15, List(3))
      OrderService.addToCart(2, 4, 5, 30, 15, List(4))
      OrderService.addToCart(3, 4, 5, 30, 15, List(4))
      OrderService.addToCart(3, 3, 6, 30, 15, List(3))
      OrderService.addToCart(3, 3, 6, 30, 15, List(5))
      OrderService.addToCart(3, 3, 6, 30, 15, List(3))
      OrderService.confirmCart(1) must beTrue
      OrderService.confirmCart(2) must beTrue
      OrderService.confirmCart(3) must beTrue
      OrderService.getOrdersByCustID(1).length must be equalTo(1)
      OrderService.getOrdersByCustID(2).length must be equalTo(1)
      OrderService.getOrdersByCustID(3).length must be equalTo(1)
    }

    "Return all orders" in memDB {
      clearDatabase()
      OrderService.addToCart(1, 4, 5, 30, 15, List(4))
      OrderService.addToCart(1, 3, 6, 30, 15, List(3))
      OrderService.addToCart(1, 3, 6, 30, 15, List(5))
      OrderService.addToCart(2, 3, 6, 30, 15, List(3))
      OrderService.addToCart(2, 4, 5, 30, 15, List(4))
      OrderService.addToCart(3, 4, 5, 30, 15, List(4))
      OrderService.addToCart(3, 3, 6, 30, 15, List(3))
      OrderService.addToCart(3, 3, 6, 30, 15, List(5))
      OrderService.addToCart(3, 3, 6, 30, 15, List(3))
      OrderService.addToCart(4, 3, 6, 30, 15, List(3))
      OrderService.addToCart(5, 3, 6, 30, 15, List(3))
      OrderService.confirmCart(1) must beTrue
      OrderService.confirmCart(2) must beTrue
      OrderService.confirmCart(3) must beTrue
      OrderService.confirmCart(4) must beTrue
      OrderService.getAllOrders.length must be equalTo(4)
    }

    "Return the total business volume by CustID" in memDB {
      clearDatabase()
      OrderService.addToCart(1, 4, 5, 30, 15, List(4))
      OrderService.addToCart(1, 3, 6, 30, 15, List(3))
      OrderService.addToCart(1, 3, 6, 30, 15, List(5))
      OrderService.addToCart(2, 3, 6, 30, 15, List(3))
      OrderService.addToCart(2, 4, 5, 30, 15, List(4))
      OrderService.addToCart(3, 4, 5, 30, 15, List(4))
      OrderService.addToCart(3, 3, 6, 30, 15, List(3))
      OrderService.addToCart(3, 3, 6, 30, 15, List(5))
      OrderService.addToCart(3, 3, 6, 30, 15, List(3))
      OrderService.addToCart(4, 3, 6, 30, 15, List(3))
      OrderService.confirmCart(1) must beTrue
      OrderService.confirmCart(2) must beTrue
      OrderService.confirmCart(3) must beTrue
      OrderService.confirmCart(4) must beTrue
      OrderService.getTotalBusinessVolumeByCustID(1) must be equalTo(16270)
      OrderService.getTotalBusinessVolumeByCustID(2) must be equalTo(12010)
      OrderService.getTotalBusinessVolumeByCustID(3) must be equalTo(20530)
      OrderService.getTotalBusinessVolumeByCustID(4) must be equalTo(4260)
      OrderService.getAverageBusinessVolume(Option(5)) must be equalTo(0)
    }

    //ToDo: Was genau macht die Methode?
    "Return the avarage business volume" in memDB {
      clearDatabase()
      OrderService.addToCart(1, 4, 5, 30, 15, List(4))
      OrderService.addToCart(1, 3, 6, 30, 15, List(3))
      OrderService.addToCart(1, 3, 6, 30, 15, List(5))
      OrderService.addToCart(2, 3, 6, 30, 15, List(3))
      OrderService.addToCart(2, 4, 5, 30, 15, List(4))
      OrderService.addToCart(3, 4, 5, 30, 15, List(4))
      OrderService.addToCart(3, 3, 6, 30, 15, List(3))
      OrderService.addToCart(3, 3, 6, 30, 15, List(5))
      OrderService.addToCart(3, 3, 6, 30, 15, List(3))
      OrderService.addToCart(4, 3, 6, 30, 15, List(3))
      OrderService.confirmCart(1) must beTrue
      OrderService.confirmCart(2) must beTrue
      OrderService.confirmCart(3) must beTrue
      OrderService.confirmCart(4) must beTrue
      OrderService.getAverageBusinessVolume(Option(1)) must be equalTo(16270)
      OrderService.getAverageBusinessVolume(Option(2)) must be equalTo(12010)
      OrderService.getAverageBusinessVolume(Option(3)) must be equalTo(20530)
      OrderService.getAverageBusinessVolume(Option(4)) must be equalTo(4260)
      OrderService.getAverageBusinessVolume(Option(5)) must be equalTo(0)
    }

    "Return total business volume" in memDB {
      clearDatabase()
      OrderService.addToCart(1, 4, 5, 30, 15, List(4))
      OrderService.addToCart(1, 3, 6, 30, 15, List(3))
      OrderService.addToCart(1, 3, 6, 30, 15, List(5))
      OrderService.addToCart(2, 3, 6, 30, 15, List(3))
      OrderService.addToCart(2, 4, 5, 30, 15, List(4))
      OrderService.addToCart(3, 4, 5, 30, 15, List(4))
      OrderService.addToCart(3, 3, 6, 30, 15, List(3))
      OrderService.addToCart(3, 3, 6, 30, 15, List(5))
      OrderService.addToCart(3, 3, 6, 30, 15, List(3))
      OrderService.addToCart(4, 3, 6, 30, 15, List(3))
      OrderService.confirmCart(1) must beTrue
      OrderService.confirmCart(2) must beTrue
      OrderService.confirmCart(3) must beTrue
      OrderService.confirmCart(4) must beTrue
      OrderService.getTotalBusinessVolume must be equalTo(53070)
    }

    "Return the price of the costs in String" in memDB {
      clearDatabase()
      OrderService.costsToString(53070) must be equalTo("530,70 €")
    }



  }



}