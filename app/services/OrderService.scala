package services

import dbaccess.{OrderDao, OrderDaoT}
import models._

/**
 * Service class for user related operations.
 *
 * @author ob, scs
 */
trait OrderServiceT {

  val orderDao: OrderDaoT = OrderDao

  /**
    * Adds a new order to the system.
    * @param id id of the new order.
    * @return the new order.
    */
  def addOrder(custID: Long, itemID: Long, quantity: Int, size: Int, costs: Int): Order = {
    // create Category
    val newOrderList : List[OrderItem] = List(OrderItem(itemID, "name", quantity, size, costs))
    val newOrder = Order(-1, custID, null, newOrderList, costs)
    // persist and return Order
    orderDao.addOrder(newOrder)
  }

  /**
    * Gets a list of all orders of a customer.
    * @return list of orders.
    */
  def getOrdersByCustID(custID: Long): List[Order] = {
    orderDao.getOrdersByCustID(custID)
  }

}

object OrderService extends OrderServiceT
