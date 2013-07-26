package com.sysgears.example.dao

import com.sysgears.example.config.Configuration
import com.sysgears.example.domain._
import java.sql._
import scala.Some
import scala.slick.driver.MySQLDriver.simple.Database.threadLocalSession
import scala.slick.driver.MySQLDriver.simple._
import slick.jdbc.meta.MTable

/**
 * Provides DAL for Customer entities for MySQL database.
 */
class CustomerDAO extends Configuration {

  // init Database instance
  private val db = Database.forURL(url = "jdbc:mysql://%s:%d/%s".format(dbHost, dbPort, dbName),
    user = dbUser, password = dbPassword, driver = "com.mysql.jdbc.Driver")

  // create tables if not exist
  db.withSession {
    if (MTable.getTables("customers").list().isEmpty) {
      Customers.ddl.create
    }
  }

  /**
   * Saves customer entity into database.
   *
   * @param customer customer entity to
   * @return saved customer entity
   */
  def create(customer: Customer): Either[Failure, Customer] = {
    try {
      val id = db.withSession {
        Customers returning Customers.id insert customer
      }
      Right(customer.copy(id = Some(id)))
    } catch {
      case e: SQLException =>
        Left(databaseError(e))
    }
  }

  /**
   * Updates customer entity with specified one.
   *
   * @param id       id of the customer to update.
   * @param customer updated customer entity
   * @return updated customer entity
   */
  def update(id: Long, customer: Customer): Either[Failure, Customer] = {
    try
      db.withSession {
        Customers.where(_.id === id) update customer.copy(id = Some(id)) match {
          case 0 => Left(notFoundError(id))
          case _ => Right(customer.copy(id = Some(id)))
        }
      }
    catch {
      case e: SQLException =>
        Left(databaseError(e))
    }
  }

  /**
   * Deletes customer from database.
   *
   * @param id id of the customer to delete
   * @return deleted customer entity
   */
  def delete(id: Long): Either[Failure, Customer] = {
    try {
      db.withTransaction {
        val query = Customers.where(_.id === id)
        val customers = query.run.asInstanceOf[List[Customer]]
        customers.size match {
          case 0 =>
            Left(notFoundError(id))
          case _ => {
            query.delete
            Right(customers.head)
          }
        }
      }
    } catch {
      case e: SQLException =>
        Left(databaseError(e))
    }
  }

  /**
   * Retrieves specific customer from database.
   *
   * @param id id of the customer to retrieve
   * @return customer entity with specified id
   */
  def get(id: Long): Either[Failure, Customer] = {
    try {
      db.withSession {
        Customers.findById(id).firstOption match {
          case Some(customer: Customer) =>
            Right(customer)
          case _ =>
            Left(notFoundError(id))
        }
      }
    } catch {
      case e: SQLException =>
        Left(databaseError(e))
    }
  }

  /**
   * Retrieves list of customers with specified parameters from database.
   *
   * @param params search parameters
   * @return list of customers that match given parameters
   */
  def search(params: CustomerSearchParameters): Either[Failure, List[Customer]] = {
    implicit val typeMapper = Customers.dateTypeMapper

    try {
      db.withSession {
        val query = for {
          customer <- Customers if {
          Seq(
            params.firstName.map(customer.firstName is _),
            params.lastName.map(customer.lastName is _),
            params.birthday.map(customer.birthday is _)
          ).flatten match {
            case Nil => ConstColumn.TRUE
            case seq => seq.reduce(_ && _)
          }
        }
        } yield customer

        Right(query.run.toList)
      }
    } catch {
      case e: SQLException =>
        Left(databaseError(e))
    }
  }

  /**
   * Produce database error description.
   *
   * @param e SQL Exception
   * @return database error description
   */
  protected def databaseError(e: SQLException) =
    Failure("%d: %s".format(e.getErrorCode, e.getMessage), FailureType.DatabaseFailure)

  /**
   * Produce customer not found error description.
   *
   * @param customerId id of the customer
   * @return not found error description
   */
  protected def notFoundError(customerId: Long) =
    Failure("Customer with id=%d does not exist".format(customerId), FailureType.NotFound)
}