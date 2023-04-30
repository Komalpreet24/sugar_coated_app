package com.komal.sugarcoated.utils

object Constants {

  const val ONE_SECOND: Long              = 1000
  const val FIVE_MINUTES: Long            = 300000
  const val EMAIL_REGEX: String           = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
  const val PASSWORD_REGEX: String        = "(?=.*[0-9a-zA-Z]).{6,}"
  const val DATE_TIME_FORMAT: String      = "MMMM dd, yyyy hh:mm a"
  const val DATE_FORMAT: String           = "MMMM dd, yyyy"
  const val TIME_FORMAT: String           = "hh:mm a"
  const val BREAKFAST: String             = "Breakfast"
  const val LUNCH: String                 = "Lunch"
  const val SNACK: String                 = "Snack"
  const val DINNER: String                = "Dinner"
  const val FASTING: String               = "Fasting"
  const val RANDOM: String                = "Random"
  const val BED_TIME: String              = "Bed Time"
  const val SENSOR_CHANGE: String         = "sensorChangeDate"
  const val INFUSION_SET_CHANGE: String   = "infusionSetChangeDate"
  const val SENSOR_SUPPLIES: String       = "sensorSupplies"
  const val INFUSION_SET_SUPPLIES: String = "infusionSetSupplies"
  const val INSULIN_SUPPLIES: String      = "insulinSupplies"

  //TAGS
  const val SIGNUP: String              = "SIGNUP"
  const val LOGIN: String               = "LOGIN"
  const val SAVE_RECORD: String         = "SAVE_RECORD"
  const val FETCH_RECORDS: String       = "FETCH_RECORDS"
  const val EVENT_LOG: String           = "EVENT_LOG"
  const val USER_DATA: String           = "USER_DATA"
  const val SAVE_SUPPLIES: String       = "SAVE_SUPPLIES"

  //SUCCESS CONSTANTS
  const val SIGNUP_SUCCESS: String        = "SIGNUP_SUCCESS"
  const val LOGIN_SUCCESS: String         = "LOGIN_SUCCESS"
  const val LOGOUT_SUCCESS: String        = "LOGOUT_SUCCESS"
  const val RESET: String                 = "RESET"
  const val SAVE_RECORD_SUCCESS: String   = "SAVE_RECORD_SUCCESS"
  const val SAVE_SUPPLIES_SUCCESS: String = "SAVE_SUPPLIES_SUCCESS"

}