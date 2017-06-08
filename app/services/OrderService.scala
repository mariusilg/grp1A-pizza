package services

import dbaccess.{OrderDao, OrderDaoT}
import models._

/**
 * Service class for order related operations.
 *
 * @author ne
 */
trait OrderServiceT {

  val orderDao: OrderDaoT = OrderDao

  /**
    * Adds a new product to the users system.
    * @param custID id of the customer.
    * @param itemID
    * @param quantity
    * @param size
    * @param distance
    * @param extraIDs
    * @return the new order.
    */
  def addToCart(custID: Long, itemID: Long, quantity: Int, size: Int, distance: Int, extraIDs: List[Long]): Unit = {
    val item = ItemService.getItem(itemID).get
    var newOrderExtras = List[OrderExtra]()
    for(id <- extraIDs) {
      val extra = ExtraService.getExtra(id)
      extra match {
        case Some(extra) => newOrderExtras = OrderExtra(-1, extra.id, extra.name, 1, extra.price) :: newOrderExtras
        case _ =>
      }
    }
    var newOrderItems = List[OrderItem](OrderItem(-1, itemID, item.name, quantity, size, CategoryService.getUnit(item.categoryID), newOrderExtras, calcProductCost(quantity, size, item.price)))
    var newOrder = Order(-1, custID, "inCart", null, newOrderItems, distance, -1, -1)
    newOrder.calcDuration(item.prepDuration)
    newOrder.calcOrderCost

    val cartID = getCartIDByUserID(custID)
    cartID match {
      case Some(cartID) => orderDao.addOrderItems(newOrder.orderItems, cartID)
      case _ => orderDao.addOrder(newOrder)
    }
  }

  def confirmCart(custID: Long): Boolean = {
    val cart = getCartByUserID(custID).head
    orderDao.confirmCart(cart)
  }

  def getRecentOrderByCustID(custID: Long): Order = {
    orderDao.getOrdersByCustID(custID).head
  }

  def deleteCart(custID: Long): Boolean = {
    val cart = getCartByUserID(custID).head
    orderDao.deleteCart(cart.id)
  }

  def deleteCartItem(custID: Long, orderItemID: Long): Boolean = {
    val cart = getCartByUserID(custID).head
    if(cart.orderItems.length == 1) {
      deleteCart(custID)
    } else {
      orderDao.deleteOrderItem(cart.id, orderItemID)
    }
  }

  def deleteCartExtra(custID: Long, orderExtraID: Long): Boolean = {
    val cart = getCartByUserID(custID).head
    orderDao.deleteOrderExtra(cart.id, orderExtraID)
  }


  def cancelOrder(custID: Long, orderID: Long): Boolean = {
    orderDao.cancelOrder(custID, orderID)
  }

  def cancelOrder(orderID: Long): Boolean = {
    orderDao.cancelOrder(orderID)
  }

  def acceptOrder(orderID: Long): Boolean = {
    orderDao.acceptOrder(orderID)
  }


  def getCartIDByUserID(userID: Long): Option[Long] = {
    orderDao.getCartIDByUserID(userID)
  }

  def getCartByUserID(userID: Long): List[Order] = {
    var orders = orderDao.getCartByCustID(userID)
    for(order <- orders) {
      order.calcOrderCost
    }
    orders
  }

  /*def changeState(custID: Long, newState: String) = {
    orderDao.changeState(custID, newState)
  }*/

  def calcProductCost(quantity: Int, size: Int, price: Int): Int = {
    val costs = quantity * size * price
    costs
  }

  /**
    * Gets a list of all orders of a customer.
    * @return list of orders.
    */
  def getOrdersByCustID(custID: Long): List[Order] = {
    orderDao.getOrdersByCustID(custID)
  }

  def getAllOrders: List[Order] = {
    orderDao.getAllOrders
  }

  def getTotalBusinessVolumeByCustID(id: Long) : Int = {
    val turnover = orderDao.getTotalBusinessVolumeByCustID(id)
    turnover match {
      case Some(turnover) => turnover
      case None => 0
    }
  }

  def getAverageBusinessVolume(id: Option[Long]) : Int = {
    id match {
      case Some(id) => orderDao.getAverageBusinessVolume(id)
      case None => orderDao.getAverageBusinessVolume
    }
  }



  def getTotalBusinessVolume : Int = {
    val turnover = orderDao.getTotalBusinessVolume
    turnover match {
      case Some(turnover) => turnover
      case None => 0
    }
  }

  def costsToString(cents: Int) : String  =  {
    "%.2f €".format(cents.toDouble/100)
  }

}

object OrderService extends OrderServiceT
