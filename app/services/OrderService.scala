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
    * Adds a new order to the system.
    * @param id id of the new order.
    * @return the new order.
    */
  def addOrder(custID: Long, itemID: Long, quantity: Int, size: Int, extraIDs: List[Long]): Order = {
    val item = ItemService.getItem(itemID).get
    var newOrderExtras = List[OrderExtra]()
    for(id <- extraIDs) {
      val extra = ExtraService.getExtra(id).get
      newOrderExtras = OrderExtra(extra.id, extra.name, 1, extra.price) :: newOrderExtras
    }
    var newOrderItems = List[OrderItem](OrderItem(itemID, item.name, quantity, size, newOrderExtras, calcProductCost(quantity, size, item.price)))
    val cost = calcOrderCost(newOrderItems)
    var newOrder = Order(-1, custID, null, newOrderItems, cost)
    orderDao.addOrder(newOrder)
  }

  def calcProductCost(quantity: Int, size: Int, price: Int): Int = {
    val costs = quantity * size * price
    costs
  }

  def calcOrderCost(orderItems: List[OrderItem]) : Int = {
      var sum: Int = 0
      for(orderItem <- orderItems) {
        for(orderExtra <- orderItem.orderExtras){
          sum += orderExtra.price * orderItem.quantity
        }
        sum += orderItem.price
      }
      sum
  }

  def bkpaddOrder(custID: Long, itemID: Long, quantity: Int, size: Int, eQuantity: Option[Int], extraID: Option[Long]): Order = {
    val item = ItemService.getItem(itemID).get
    var newOrderExtras = List[OrderExtra]()
    eQuantity match{
      case Some(eQuantity) => extraID match{
        case Some(extraID) => val extra = ExtraService.getExtra(extraID).get
          newOrderExtras = OrderExtra(extra.id, extra.name, eQuantity, eQuantity * extra.price) :: newOrderExtras
        case None =>
      }
      case None =>
    }
    var newOrderItems = List[OrderItem](OrderItem(itemID, item.name, quantity, size, newOrderExtras, quantity * size * item.price))
    var costs: Int = {
      var sum: Int = 0
      for(orderItem <- newOrderItems) {
        for(orderExtra <- orderItem.orderExtras){
          sum += orderExtra.price * orderItem.quantity
        }
        sum += orderItem.price
      }
      sum
    }

    var newOrder = Order(-1, custID, null, newOrderItems, costs)
    orderDao.addOrder(newOrder)
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

  def getTotalBusinessVolume : Int = {
    val turnover = orderDao.getTotalBusinessVolume
    turnover match {
      case Some(turnover) => turnover
      case None => 0
    }
  }

  def costsToString(cents: Int) : String  =  {
    "%.2f€".format(cents.toDouble/100)
  }

}

object OrderService extends OrderServiceT
