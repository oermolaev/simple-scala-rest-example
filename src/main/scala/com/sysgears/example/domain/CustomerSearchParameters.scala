package com.sysgears.example.domain

import java.util.Date

/**
 * Customers search parameters.
 *
 * @param firstName first name
 * @param lastName  last name
 * @param birthday  date of birth
 */
case class CustomerSearchParameters(firstName: Option[String] = None,
                                    lastName: Option[String] = None,
                                    birthday: Option[Date] = None)