package models

case class Order(id: Long, custID: Long, itemID: Long, size: Int, quantity: Int , costs: Int)