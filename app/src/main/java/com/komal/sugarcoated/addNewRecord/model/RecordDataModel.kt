package com.komal.sugarcoated.addNewRecord.model

import java.util.*

data class RecordDataModel(
  val bloodSugarValue: String = "",
  val category: String        = "",
  val dateAndTime: Date       = Date(),
  val note: String            = "",
  var userId: String          = ""
)
