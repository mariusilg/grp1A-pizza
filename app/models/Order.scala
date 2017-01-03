package models
import java.util.Date
/**
  * Order entity.
  * @param id database id of the order.
  * @param custID database id of the customer.
  * @param orderItems item of order
  * @param costs costs of order.
  */
case class Order(var id: Long, var custID: Long, var date: Date, var orderItems: List[OrderItem], var costs: Int) {

  def costsToString : String = {
    "%.2f €".format(this.costs.toDouble/100)
  }

}