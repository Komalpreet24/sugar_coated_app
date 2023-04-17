package com.komal.sugarcoated.signup.model

import com.google.firebase.firestore.DocumentId
import com.komal.sugarcoated.addNewRecord.model.RecordDataModel

data class UserSignUpData(
  @DocumentId
  val id: String? = "",
  val name: String? = "",
  val email: String? = "",
  val webViewLink: String? = "",
  val recordList: RecordList? = null
)

data class RecordList(
  var recordList: ArrayList<RecordDataModel> = arrayListOf()
)