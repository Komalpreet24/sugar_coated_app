package com.komal.sugarcoated.utils

object Constants {

  const val ONE_SECOND: Long          = 1000
  const val FIVE_MINUTES: Long        = 300000
  const val EMAIL_REGEX: String       = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
  const val PASSWORD_REGEX: String    = "(?=.*[0-9a-zA-Z]).{6,}"
  const val DATE_TIME_FORMAT: String  = "MMMM dd, yyyy hh:mm a"
  const val DATE_FORMAT: String       = "MMMM dd, yyyy"
  const val TIME_FORMAT: String       = "hh:mm a"

  //TAGS
  const val SIGNUP: String        = "SIGNUP"
  const val LOGIN: String         = "LOGIN"
  const val SAVE_RECORD: String   = "SAVE_RECORD"

  //SUCCESS CONSTANTS
  const val SIGNUP_SUCCESS: String      = "SIGNUP_SUCCESS"
  const val LOGIN_SUCCESS: String       = "LOGIN_SUCCESS"
  const val LOGOUT_SUCCESS: String      = "LOGOUT_SUCCESS"
  const val RESET: String               = "RESET"
  const val SAVE_RECORD_SUCCESS: String = "SAVE_RECORD_SUCCESS"

}