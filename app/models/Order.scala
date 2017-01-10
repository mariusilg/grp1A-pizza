package models
import java.util.Date
/**
  * Order entity.
  * @param id database id of the order.
  * @param custID database id of the customer.
  * @param orderItems item of order
  * @param distance distance of customer.
  * @param duration
  * @param costs costs of order.
  */
case class Order(var id: Long, var custID: Long, var date: Date, var orderItems: List[OrderItem], var distance: Int, var duration: Int, var costs: Int) {

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

  def calcDuration(prepDuration: Int) : Unit = {
    this.duration = prepDuration + ( this.distance * 2 )
  }

  def getDuration : Int = this.duration
}