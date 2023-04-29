package com.komal.sugarcoated.utils

import android.content.Context
import android.content.SharedPreferences

class AppSharedPreferences private constructor(context: Context){

  private val sharedPref: SharedPreferences = context.getSharedPreferences(
    "sugar_coated_preferences", Context.MODE_MULTI_PROCESS
  )

  companion object {
    private var instance: AppSharedPreferences? = null
    fun getInstance(context: Context): AppSharedPreferences? {
      if (instance == null) {
        synchronized(AppSharedPreferences::class.java) {
          if (instance == null)
            instance = AppSharedPreferences(context)
        }
      }
      return instance
    }
  }

  fun setUserId(userId: String?) =
    sharedPref.edit().putString("userId", userId).apply()

  val userId = sharedPref.getString("userId", "")

  fun setToken(token: String?) =
    sharedPref.edit().putString("token", token).apply()

  val token = sharedPref.getString("token", "")

  fun setLoggedInStatus(isLoggedIn: Boolean) =
    sharedPref.edit().putBoolean("isLoggedIn", isLoggedIn).apply()

  val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

  fun setTodayForSensorChange(isSensorChangedToday: Boolean) =
    sharedPref.edit().putBoolean("isSensorChangedToday", isSensorChangedToday).apply()

  val isSensorChangedToday = sharedPref.getBoolean("isSensorChangedToday", false)

  fun setTodayForInfusionSetChange(isInfusionSetChangedToday: Boolean) =
    sharedPref.edit().putBoolean("isInfusionSetChangedToday", isInfusionSetChangedToday).apply()

  val isInfusionSetChangedToday = sharedPref.getBoolean("isInfusionSetChangedToday", false)

  fun resetLoginData() {
    sharedPref.edit().clear().apply()
  }

}