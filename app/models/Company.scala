package models

/**
 * The Pizza Service company.
 *
 * @author ob, scs, ne
 */
object Company {

  final val name = "Don Corleone"
  final val street = "Lothstraße.64"
  final val zip = "80335"
  final val city = "Munich"
  final val link = "http://localhost:9000"

  //TODO: Mail Configuration
  final val mailServer = "postout.lrz.de"
  final val email = "n.engelbrecht1@hm.edu"
  final val mailUser = "hm-nengelbr"
  final val mailPassword = "xxx"
  final val mailPort: Int = 587
}
