package models

case class Item(var id: Long, var name: String, var price: Int)

case class OrderItem(var id: Long, var name: String, var quantity: Int, size: Int, var price: Int)