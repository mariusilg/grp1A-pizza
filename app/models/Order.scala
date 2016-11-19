package models

case class Order(id: Long, custID: Long, itemID: Long, quantity: Int , costs: Int)