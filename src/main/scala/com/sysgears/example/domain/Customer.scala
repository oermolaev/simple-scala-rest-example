package com.sysgears.example.domain

import scala.slick.driver.MySQLDriver.simple._

/**
 * Customer entity.
 *
 * @param id        unique id
 * @param firstName first name
 * @param lastName  last name
 * @param birthday  date of birth
 */
case class Customer(id: Option[Long], firstName: String, lastName: String, birthday: Option[java.util.Date])

/**
 * Mapped customers table object.
 */
object Customers extends Table[Customer]("customers") {

  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def firstName = column[String]("first_name")

  def lastName = column[String]("last_name")

  def birthday = column[java.util.Date]("birthday", O.Nullable)

  def * = id.? ~ firstName ~ lastName ~ birthday.? <>(Customer, Customer.unapply _)

  implicit val dateTypeMapper = MappedTypeMapper.base[java.util.Date, java.sql.Date](
  {
    ud => new java.sql.Date(ud.getTime)
  }, {
    sd => new java.util.Date(sd.getTime)
  })

  val findById = for {
    id <- Parameters[Long]
    c <- this if c.id is id
  } yield c
}