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
  def addOrder(custID: Long, itemID: Long, quantity: Int, size: Int, distance: Int, extraIDs: List[Long]): Order = {
    val item = ItemService.getItem(itemID).get
    var newOrderExtras = List[OrderExtra]()
    for(id <- extraIDs) {
      val extra = ExtraService.getExtra(id)
      extra match {
        case Some(extra) => newOrderExtras = OrderExtra(extra.id, extra.name, 1, extra.price) :: newOrderExtras
        case None =>
      }
    }
    var newOrderItems = List[OrderItem](OrderItem(itemID, item.name, quantity, size, CategoryService.getUnit(item.categoryID), newOrderExtras, calcProductCost(quantity, size, item.price)))
    var newOrder = Order(-1, custID, null, newOrderItems, distance, -1, -1)
    newOrder.calcDuration(item.prepDuration)
    newOrder.calcOrderCost
    orderDao.addOrder(newOrder)
  }

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
