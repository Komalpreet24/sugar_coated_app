package com.komal.sugarcoated.addNewRecord.model

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class RecordDataModel(
  @ServerTimestamp
  val timestamp: Date,
  val bloodSugarValue: String? = "",
  val label: String? = "",
  val dateAndTime: String? = "",
  val Note: String? = ""
)
