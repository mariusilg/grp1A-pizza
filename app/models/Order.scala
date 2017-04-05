package models
import java.util.Date

/**
  * Order entity.
  * @param id database id of the order.
  * @param custID database id of the customer.
  * @param state state of order.
  * @param orderItems list of items belonging to this order
  * @param distance current distance of customer.
  * @param duration duration until delivery.
  * @param costs costs of order.
  */
case class Order(var id: Long, var custID: Long, var state: String, var date: Date, var orderItems: List[OrderItem], var distance: Int, var duration: Int, var costs: Int) {

  /*
   * Method to convert this order price into a String.
   */
  def costsToString : String = {
    "%.2f €".format(this.costs.toDouble/100)
  }


  /*def calcDuration(items: List[models.Item]) : Unit = {
    var prepDuration = 0
    for(item <- items) {
      if(item.prepDuration > prepDuration)
        prepDuration = item.prepDuration
    }
    this.duration = prepDuration + ( this.distance * 2 )
    /*for(item <- this.orderItems) {
      duration += 10
    }
    this.duration = duration + ( this.distance * 2 )*/
  }*/

  /**
   * Method to calculate duration until delivery.
   * @param prepDuration
   */
  def calcDuration(prepDuration: Int) : Unit = {
    this.duration = prepDuration + ( this.distance * 2 )
  }

  /**
    * Method to calculate total order costs.
    */
  def calcOrderCost : Unit = {
    var sum: Int = 0
    for(orderItem <- this.orderItems) {
      for(orderExtra <- orderItem.orderExtras){
        sum += orderExtra.price * orderItem.quantity
      }
      sum += orderItem.price
    }
    this.costs = sum
  }

  def getDuration : Int = this.duration
}