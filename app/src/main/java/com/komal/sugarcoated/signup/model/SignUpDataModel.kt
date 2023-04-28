package com.komal.sugarcoated.signup.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class UserSignUpData(
  @DocumentId
  val id: String = "",
  val name: String = "",
  val email: String = "",
  val webViewLink: String = "",
  val sensorChangeDate: Date = Date(),
  val infusionSetChangeDate: Date = Date()
)