package com.komal.sugarcoated.utils

import android.app.Dialog
import android.content.Context
import android.widget.Toast
import com.komal.sugarcoated.R
import com.komal.sugarcoated.utils.Constants.EMAIL_REGEX
import com.komal.sugarcoated.utils.Constants.PASSWORD_REGEX

private var progressBar: Dialog? = null

fun showProgress(context: Context?) {
  show(context)
}

fun hideProgress() {
  hide()
}

fun showToast(context: Context, msg: String) {
  Toast.makeText(context,msg, Toast.LENGTH_LONG).show()
}

fun isEmailValid(email: String): Boolean{
    return email.matches(EMAIL_REGEX.toRegex())
}

fun isPasswordValid(password: String): Boolean{
  return password.matches(PASSWORD_REGEX.toRegex())
}

/*---Private Functions---*/

private fun show(context: Context?) {
  if (context != null) {
    progressBar = Dialog(context)
    progressBar?.setContentView(R.layout.progress_bar)
    progressBar?.window?.setBackgroundDrawableResource(R.color.Transparent)
    progressBar?.show()
  }
}

private fun hide() {
  if (progressBar != null && progressBar?.isShowing == true) {
      progressBar?.dismiss()
      progressBar = null
  }
}
