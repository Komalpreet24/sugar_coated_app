package com.komal.sugarcoated.signup.model

import com.google.firebase.firestore.DocumentId

data class UserSignUpData(
  @DocumentId
  val id: String = "",
  val name: String = "",
  val email: String = "",
  val webViewLink: String = "",
)