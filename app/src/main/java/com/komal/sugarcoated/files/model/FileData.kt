package com.komal.sugarcoated.files.model

import java.util.*

data class FileData (
  val name: String = "",
  val url: String = "",
  val userId: String = "",
  val dateAndTime: Date = Date()
)