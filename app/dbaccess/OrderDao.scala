package dbaccess

import anorm._
import play.api.Play.current
import play.api.db.DB
import anorm.NamedParameter.symbol
import models._
import java.util.Date

/**
 * Data access object for order related operations.
 *
 * @author ne
 */
trait OrderDaoT {

  def addOrder(order: Order): Order = {
    DB.withConnection { implicit c =>
      val id: Option[Long] =
        SQL("insert into Orders(cust_id, state, distance, duration, costs) values ({cust_id}, {state}, {distance}, {duration}, {costs})").on(
          'cust_id -> order.custID, 'state -> order.state, 'distance -> order.distance, 'duration -> order.duration, 'costs -> order.costs).executeInsert()
      order.id = id.get
    }
    addOrderItems(order.orderItems, order.id)
    order
  }

  def getCartIDByUserID(userID: Long): Option[Long] = {
    DB.withConnection { implicit c =>
      val checkUser = SQL("Select id from Orders where cust_id = {id} and state = 'inCart' limit 1;").on('id -> userID).apply
        .headOption
      checkUser match {
        case Some(row) => Some(row[Long]("id"))
        case None => None
      }
    }
  }

  /**
    * Confirms cart to be an order.
    * @param cart cart.
    * @return whether update was successful or not
    */
  def confirmCart(cart: Order): Boolean = {
    DB.withConnection { implicit c =>
      val rowsUpdated = SQL("update Orders SET state = 'inOrder' , costs = {costs} where id = {id}").on('costs -> cart.costs,'id -> cart.id).executeUpdate()
      rowsUpdated == 1
    }
  }

  /**
    * Confirms cart to be an order.
    * @param cart cart.
    * @return whether update was successful or not
    */
  def cancelOrder(custID: Long, orderID: Long): Boolean = {
    DB.withConnection { implicit c =>
      val rowsUpdated = SQL("update Orders SET state = 'canceled' where id = {id} and cust_id = {custID}").on('id -> orderID,'custID -> custID).executeUpdate()
      rowsUpdated == 1
    }
  }

  /**
    * Deletes cart.
    * @param cartID order id of cart.
    * @return whether deletion was successful or not
    */
  def deleteCart(cartID: Long): Boolean = {
    DB.withConnection { implicit c =>
      val rowsDeleted = SQL("delete Orders where id = {id}").on('id -> cartID).executeUpdate()
      rowsDeleted == 1
    }
  }

  /**
    * Deletes cart.
    * @param cartID order id of cart.
    * @param cartItemID id of orderItem of cart.
    * @return whether deletion was successful or not
    */
  def deleteOrderItem(cartID: Long, cartItemID: Long): Boolean = {
    DB.withConnection { implicit c =>
      val rowsDeleted = SQL("delete Order_Items where id = {cartItemID} and order_id = {cartID}").on('cartItemID -> cartItemID, 'cartID -> cartID).executeUpdate()
      rowsDeleted == 1
    }
  }



  def getCartByCustID(custID: Long): List[Order] = {
    DB.withConnection { implicit c =>
      val selectOrdersByCustID = SQL("Select id, state, order_date, distance, duration, costs from Orders where cust_id = {custID} and state = 'inCart' limit 1;").on('custID -> custID)
      val ordersByCustID = selectOrdersByCustID().map(row => Order(row[Long]("id"), custID, row[String]("state"), row[Date]("order_date"), getOrderItemsByOrderID(row[Long]("id")), row[Int]("distance"), row[Int]("duration"), row[Int]("costs"))).toList
      ordersByCustID
    }
  }

  def addOrderItems(orderItems: List[OrderItem], orderID: Long): Unit = {
    for(orderItem <- orderItems) {
      DB.withConnection { implicit c =>
        val id: Option[Long] =
            SQL("insert into order_items(order_id, item_id, item_name, size, unit, quantity, costs) values ({order_id}, {item_id}, {item_name}, {size}, {unit}, {quantity}, {costs})").on('order_id -> orderID,
              'item_id -> orderItem.itemID, 'item_name -> orderItem.name, 'size -> orderItem.size, 'unit -> orderItem.unit, 'quantity -> orderItem.quantity, 'costs -> orderItem.price).executeInsert()
        addItemExtras(orderItem.orderExtras, id.get)
        }
    }
  }

  def addItemExtras(itemExtras: List[OrderExtra], orderItemID: Long): Unit = {
    for(itemExtra <- itemExtras) {
      DB.withConnection { implicit c =>
        val id: Option[Long] =
          SQL("insert into order_extras(order_item_id, extra_id, extra_name, quantity, costs) values ({orderItemID}, {extraID}, {extraName}, {quantity}, {costs})").on('orderItemID -> orderItemID,
            'extraID -> itemExtra.id, 'extraName -> itemExtra.name, 'quantity -> itemExtra.quantity, 'costs -> itemExtra.price).executeInsert()
      }
    }
  }

  def getOrdersByCustID(custID: Long): List[Order] = {
    DB.withConnection { implicit c =>
      val selectOrdersByCustID = SQL("Select id, state, order_date, distance, duration, costs from Orders where cust_id = {custID} and state <> 'inCart' order by order_date desc;").on('custID -> custID)
      val ordersByCustID = selectOrdersByCustID().map(row => Order(row[Long]("id"), custID, row[String]("state"), row[Date]("order_date"), getOrderItemsByOrderID(row[Long]("id")), row[Int]("distance"), row[Int]("duration"), row[Int]("costs"))).toList
      ordersByCustID
    }
  }

  def getAllOrders: List[Order] = {
    DB.withConnection { implicit c =>
      val selectAllOrders = SQL("Select id, cust_id, state, order_date, distance, duration, costs from Orders where state <> 'inCart' order by order_date desc;")
      val allOrders = selectAllOrders().map(row => Order(row[Long]("id"), row[Long]("cust_id"), row[String]("state"), row[Date]("order_date"), getOrderItemsByOrderID(row[Long]("id")), row[Int]("distance"), row[Int]("duration"), row[Int]("costs"))).toList
      allOrders
    }
  }

  def getOrderItemsByOrderID(orderID: Long): List[OrderItem] = {
    DB.withConnection { implicit c =>
      val selectItemsByOrderID = SQL("Select * from order_items where order_id = {orderID}").on('orderID -> orderID)
      val itemsByOrderID = selectItemsByOrderID().map(row => OrderItem(row[Long]("id"), row[Long]("item_id"), row[String]("item_name"), row[Int]("quantity"), row[Int]("size"), row[String]("unit"), getItemExtrasByOrderItemID(row[Long]("id")), row[Int]("costs"))).toList
      itemsByOrderID
    }
  }

  def getItemExtrasByOrderItemID(orderItemID: Long): List[OrderExtra] = {
    DB.withConnection { implicit c =>
      val selectItemExtras = SQL("Select extra_id, extra_name, quantity, costs from order_extras where order_item_id = {orderItemID}").on('orderItemID -> orderItemID)
      val itemExtras = selectItemExtras().map(row => OrderExtra(row[Long]("extra_id"), row[String]("extra_name"), row[Int]("quantity"), row[Int]("costs"))).toList
      itemExtras
    }
  }

  def getTotalBusinessVolumeByCustID(custID: Long) : Option[Int] = {
    DB.withConnection { implicit c =>
      val businessVolume = SQL("Select NVL(SUM(costs),0) as turnover from orders where cust_id = {custID} and state <> 'inCart'").on('custID -> custID).apply
        .headOption
      businessVolume match {
        case Some(row) => Some(row[Long]("turnover").toInt)
        case None => None
      }
    }
  }

  def getTotalBusinessVolume : Option[Int] = {
    DB.withConnection { implicit c =>
      val businessVolume = SQL("Select NVL(SUM(costs),0) as turnover from orders where state <> 'inCart'").apply
        .headOption
      businessVolume match {
        case Some(row) => Some(row[Long]("turnover").toInt)
        case None => None
      }
    }
  }

  def getAverageBusinessVolume : Int = {
    DB.withConnection { implicit c =>
      val businessVolume = SQL("Select NVL(AVG(costs), 0) as turnover from orders where state <> 'inCart'").apply
        .headOption
      businessVolume match {
        case Some(row) => row[Long]("turnover").toInt
        case None => 0
      }
    }
  }

  def getAverageBusinessVolume(custID: Long) : Int = {
    DB.withConnection { implicit c =>
      val businessVolume = SQL("Select NVL(AVG(costs), 0) as turnover from orders where cust_id = {custID} and state <> 'inCart'").on('custID -> custID).apply
        .headOption
      businessVolume match {
        case Some(row) => row[Long]("turnover").toInt
        case None => 0
      }
    }
  }

}

object OrderDao extends OrderDaoT
