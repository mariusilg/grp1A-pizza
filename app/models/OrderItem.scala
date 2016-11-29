package models

case class OrderItem(var id: Long, var name: String, var quantity: Int, size: Int, var orderExtras: List[OrderExtra], var price: Int)