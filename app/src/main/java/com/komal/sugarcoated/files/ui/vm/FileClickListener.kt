package com.komal.sugarcoated.files.ui.vm

import com.komal.sugarcoated.files.model.FileData

interface FileClickListener {
  fun onFileClicked(file: FileData)
}
