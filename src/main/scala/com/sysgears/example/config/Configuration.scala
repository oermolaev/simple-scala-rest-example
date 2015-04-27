package com.sysgears.example.config

import com.typesafe.config.ConfigFactory

import scala.util.Try

/**
 * Holds service configuration settings.
 */
trait Configuration {

  /**
   * Application config object.
   */
  val config = ConfigFactory.load()

  /** Database host name/address. */
  lazy val dbHost = Try(config.getString("db.host")).getOrElse("localhost")

  /** Database host port number. */
  lazy val dbPort = Try(config.getInt("db.port")).getOrElse(3306)

  /** Service database name. */
  lazy val dbName = Try(config.getString("db.name")).getOrElse("rest")

  /** User name used to access database. */
  lazy val dbUser = Try(config.getString("db.user")).toOption.orNull

  /** Password for specified user and database. */
  lazy val dbPassword = Try(config.getString("db.password")).toOption.orNull
}
