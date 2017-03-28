package controllers

import play.api.Play.current
import play.api.libs.json.{JsObject, JsValue}
import play.api.libs.ws.WS
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Main controller of the Location Service application.
 *
 * @author ne
 */
object WSController extends Controller {

  private def parseGoogleJsonDistance(json: JsValue): Int = {
    val rows = (json \ "rows").as[List[JsObject]]
    val jsonDistance = (rows.head \ "elements").as[List[JsObject]].head
    (jsonDistance \ "distance" \ "value").asOpt[Int].get
  }

  private def parseGoogleJsonTraffic(json: JsValue): Int = {
    val rows = (json \ "rows").as[List[JsObject]]
    val jsonDistance = (rows.head \ "elements").as[List[JsObject]].head
    (jsonDistance \ "duration" \ "value").asOpt[Int].get
  }

  /*def distance(adress: String, zip: String, city: String) = Action.async {
    val futureInt = getDistance(adress, zip, city)
    futureInt.map(i => Ok("Distanz: " + i))
  }*/

  def getDistance(adress: String, zip: String, city: String): Future[Int] = {
    val apiUrl = "https://maps.googleapis.com/maps/api/distancematrix/json"
    val apiKey = "AIzaSyAdA0_QjIhdFNk8EL-TofQtK4xD_a6sIFc"
    val futureDistance = WS.url(apiUrl +
      "?origins=" + models.Company.street + "+" + models.Company.zip  + "+" + models.Company.city  + "+" +
      "&destinations=" + adress.replaceAll(" ", "")  + "+" + zip + "+" + city + "&mode=car&language=de-DE&key=" + apiKey).get
    futureDistance map { response =>
      val distanceNumber: Int = parseGoogleJsonDistance(response.json)
      //val updateDistance = services.UserService.updateDistanceData(email, password, distanceNumber)
      //updateDistance
      distanceNumber
    }
  }

  def updateDistance(user: models.User): Future[Int] = {
    val apiUrl = "https://maps.googleapis.com/maps/api/distancematrix/json"
    val apiKey = "AIzaSyAdA0_QjIhdFNk8EL-TofQtK4xD_a6sIFc"
    val futureDistance = WS.url(apiUrl +
      "?origins=" + models.Company.street + "+" + models.Company.zip  + "+" + models.Company.city  + "+" +
      "&destinations=" + user.street.replaceAll(" ", "")  + "+" + user.zip + "+" + user.city + "&mode=car&language=de-DE&key=" + apiKey).get
    futureDistance map { response =>
      val distanceNumber: Int = parseGoogleJsonDistance(response.json)
      //val updateDistance = services.UserService.updateDistanceData(email, password, distanceNumber)
      services.UserService.updateDistance(user.id, distanceNumber)
      distanceNumber
    }
  }

  def getTraffic(adress: String, plz: String, city: String): Future[Int] = {
    val apiUrl = "https://maps.googleapis.com/maps/api/distancematrix/json"
    val apiKey = "AIzaSyAdA0_QjIhdFNk8EL-TofQtK4xD_a6sIFc"
    val futureDistance = WS.url(apiUrl +
      "?origins=" + models.Company.street + "+" + models.Company.zip  + "+" + models.Company.city  + "+" +
      "&destinations=" + adress.replaceAll(" ", "")  + "+" + plz + "+" + city + "&mode=car&language=de-DE&key=" + apiKey).get
    futureDistance map { response =>
      val durationNumber: Int = parseGoogleJsonTraffic(response.json)
      //val updateDistance = services.UserService.updateDistanceData(email, password, distanceNumber)
      //updateDistance
      durationNumber
    }
  }

}
