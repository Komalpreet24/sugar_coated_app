package com.komal.sugarcoated.utils

import android.app.Dialog
import android.content.Context
import android.widget.Toast
import com.komal.sugarcoated.R

private var progressBar: Dialog? = null

private fun show(context: Context?) {
  if (context != null) {
    progressBar = Dialog(context)
    progressBar?.setContentView(R.layout.progress_bar)
    progressBar?.window?.setBackgroundDrawableResource(R.color.Transparent);
    progressBar?.show()
  }
}

private fun hide() {
  if (progressBar != null) {
    progressBar!!.dismiss()
  }
}

fun showProgress(context: Context?) {
  show(context)
}

fun hideProgress() {
  hide()
}

fun showToast(context: Context, msg: String) {
  Toast.makeText(context,msg, Toast.LENGTH_LONG).show()
}