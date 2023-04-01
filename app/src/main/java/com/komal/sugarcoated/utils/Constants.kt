package com.komal.sugarcoated.utils

object Constants {

  const val ONE_SECOND: Long        = 1000
  const val FIVE_MINUTES: Long      = 300000
  const val EMAIL_REGEX: String     = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"
  const val PASSWORD_REGEX: String  = "(?=.*[0-9a-zA-Z]).{6,}"

  //TAGS
  const val SIGNUP: String = "SIGNUP"
  const val LOGIN: String  = "LOGIN"

  //SUCCESS CONSTANTS
  const val SIGNUP_SUCCESS: String            = "SIGNUP_SUCCESS"
  const val LOGIN_SUCCESS: String             = "LOGIN_SUCCESS"
  const val LOGOUT_SUCCESS: String            = "LOGOUT_SUCCESS"
  const val RESET: String                     = "RESET"

}