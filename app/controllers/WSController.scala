package controllers

import play.api.Play.current
import play.api.libs.json.{JsObject, JsValue}
import play.api.libs.ws.WS
import play.api.mvc.{Action, AnyContent, Controller}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import org.apache.http._
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import java.util.ArrayList
import org.apache.http.message.BasicNameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity


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

  def sendNotification(user: models.User): Unit = {
    val url = "https://api.pushover.net/1/messages.json"
    val client = new DefaultHttpClient
    val post = new HttpPost(url)
    val nameValuePairs = new ArrayList[NameValuePair](1)
    nameValuePairs.add(new BasicNameValuePair("token", "ahodi8gfzuiis8ntesyjhwm1prgwj8"))
    nameValuePairs.add(new BasicNameValuePair("user", "u11syxingmq1k7jjqqx8dq6axu8dm8"))
    nameValuePairs.add(new BasicNameValuePair("message", "Neue Bestellung ist gerade eingegangen \n id: " + user.id + "\n name: " + user.userName + "\n"))
    post.setEntity(new UrlEncodedFormEntity(nameValuePairs))
    val response = client.execute(post)
  }

}
