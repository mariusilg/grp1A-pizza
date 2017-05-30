package controllers

import controllers.Auth.Secured
import forms.CreateExtraForm
import play.api.data.Form
import play.api.data.Forms.{mapping, number, optional, text}
import play.api.mvc.Controller
import services.ExtraService

/**
  * Created by philipp on 15.04.17.
  */
object ExtraController extends Controller with Secured {
  val extraForm = Form(
    mapping(
      "id" -> optional(number),
      "name" -> text,
      "price" -> number,
      "categoryId" -> number
    )(CreateExtraForm.apply)(CreateExtraForm.unapply))

  def editExtra(ofExtra: Option[Long]) = withUser_Employee { user => implicit request =>
    val extra = services.ExtraService.getExtra(ofExtra.get)
    Ok(views.html.detailView_Extra("edit",extra))
  }

  def insertExtra(ofExtra: Option[Long]) = withUser_Employee { user => implicit request =>
    println("hallo welt")
    //val extra = services.ExtraService.getExtra(ofExtra.get)
    Ok(views.html.detailView_Extra("insert",None))
  }

  def manageExtras = withUser_Employee { user => implicit request =>
    var extras = ExtraService.getExtras
    Ok(views.html.extra(extras))
  }

  def post_editExtra(extraType: String) = withUser_Employee { user => implicit request =>
    extraForm.bindFromRequest.fold(
      formWithErrors => {
        BadRequest(views.html.errorHandler("", Some(user), 4, request.headers("referer")))
      },
      extraData => {
        extraType match {
          case "edit" =>
            val extra = services.ExtraService.updateExtra(extraData)
          case "insert" =>
            val extra = services.ExtraService.insertExtra(extraData)
        }

        Redirect(routes.ExtraController.manageExtras).
          flashing("success" -> "User saved!")
      })
  }
}
