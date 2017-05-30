package services

import dbaccess.{ExtraDao, ExtraDaoT}
import models._
import forms.CreateExtraForm

/**
 * Service class for extra item related operations.
 *
 * @author ne
 */
trait ExtraServiceT {

  val extraDao: ExtraDaoT = ExtraDao

  /**
    * Return extra by id.
    * @return item.
    */
  def getExtra(id: Long): Option[Item] = {
    extraDao.getExtra(id)
  }

  def updateExtra(extraForm: CreateExtraForm): Item = {

    var extra = Item(extraForm.id.get, extraForm.categoryId, extraForm.name, extraForm.price, false, 0, true)
    extraDao.updateExtra(extra)
  }


  def insertExtra(extraForm: CreateExtraForm): Item = {

    var extra = Item(0, extraForm.categoryId, extraForm.name, extraForm.price, false, 0, true)
    extraDao.insertExtra(extra)
  }

  /**
    * Return a list of extras.
    * @return list of items.
    */
  def getExtras: List[Item] = {
    extraDao.getExtras
  }

  def getExtrasForCategory(cat_id: Long): List[Item] = {
    extraDao.getExtrasForCategory(cat_id)
  }

  def rmExtra(extraId: Int): String = {
    extraDao.rmExtra(extraId)
  }



}

object ExtraService extends ExtraServiceT
