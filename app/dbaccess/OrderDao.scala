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
        SQL("insert into Orders(cust_id, costs) values ({cust_id}, {costs})").on(
          'cust_id -> order.custID, 'costs -> order.costs).executeInsert()
      order.id = id.get
    }
    addOrderItems(order.orderItems, order.id)
    order
  }

  def addOrderItems(orderItems: List[OrderItem], orderID: Long): Unit = {
    for(orderItem <- orderItems) {
      DB.withConnection { implicit c =>
        val id: Option[Long] =
            SQL("insert into order_items(order_id, item_id, item_name, size, quantity, costs) values ({order_id}, {item_id}, {item_name}, {size}, {quantity}, {costs})").on('order_id -> orderID,
              'item_id -> orderItem.id, 'item_name -> orderItem.name, 'size -> orderItem.size, 'quantity -> orderItem.quantity, 'costs -> orderItem.price).executeInsert()
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
      val selectOrdersByCustID = SQL("Select id, order_date, costs from Orders where cust_id = {custID} order by order_date desc;").on('custID -> custID)
      val ordersByCustID = selectOrdersByCustID().map(row => Order(row[Long]("id"), custID, row[Date]("order_date"), getOrderItemsByOrderID(row[Long]("id")), row[Int]("costs"))).toList
      ordersByCustID
    }
  }

  def getAllOrders: List[Order] = {
    DB.withConnection { implicit c =>
      val selectAllOrders = SQL("Select id, cust_id, order_date, costs from Orders order by order_date desc;")
      val allOrders = selectAllOrders().map(row => Order(row[Long]("id"), row[Long]("cust_id"), row[Date]("order_date"), getOrderItemsByOrderID(row[Long]("id")), row[Int]("costs"))).toList
      allOrders
    }
  }

  def getOrderItemsByOrderID(orderID: Long): List[OrderItem] = {
    DB.withConnection { implicit c =>
      val selectItemsByOrderID = SQL("Select * from order_items where order_id = {orderID}").on('orderID -> orderID)
      val itemsByOrderID = selectItemsByOrderID().map(row => OrderItem(row[Long]("item_id"), row[String]("item_name"), row[Int]("quantity"), row[Int]("size"), getItemExtrasByOrderItemID(row[Long]("id")), row[Int]("costs"))).toList
      itemsByOrderID
    }
  }

  def getItemExtrasByOrderItemID(orderItemID: Long): List[OrderExtra] = {
    DB.withConnection { implicit c =>
      val selectItemExtras = SQL("Select extra_id, extra_name, quantity, costs from order_extras where  order_item_id = {orderItemID}").on('orderItemID -> orderItemID)
      val itemExtras = selectItemExtras().map(row => OrderExtra(row[Long]("extra_id"), row[String]("extra_name"), row[Int]("quantity"), row[Int]("costs"))).toList
      itemExtras
    }
  }
}

object OrderDao extends OrderDaoT
