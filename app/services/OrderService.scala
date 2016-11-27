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
  def addOrder(custID: Long, itemID: Long, quantity: Int, size: Int, costs: Int): Order = {
    val item = ItemService.getItem(itemID).get
    var newOrderItems = List[OrderItem](OrderItem(itemID, item.name, quantity, size, costs))
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

  def costsToString(cents: Int) : String  =  {
    "%.2f€".format(cents.toDouble/100)
  }

}

object OrderService extends OrderServiceT
