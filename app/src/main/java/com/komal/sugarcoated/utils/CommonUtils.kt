package com.komal.sugarcoated.utils

import android.app.Dialog
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.LineBackgroundSpan
import android.widget.Toast
import com.komal.sugarcoated.R
import com.komal.sugarcoated.utils.Constants.EMAIL_REGEX
import com.komal.sugarcoated.utils.Constants.PASSWORD_REGEX
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade

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

class EventDecorator
  (private val dates: HashSet<CalendarDay>, private val colors: IntArray) : DayViewDecorator {

  override fun shouldDecorate(day: CalendarDay): Boolean {
    return dates.contains(day)
  }

  override fun decorate(view: DayViewFacade) {
    view.addSpan(CustomMultipleDotSpan(10f, colors))
  }
}


class CustomMultipleDotSpan(private val radius: Float,
                            private var color: IntArray): LineBackgroundSpan {

  override fun drawBackground(
    canvas: Canvas, paint: Paint,
    left: Int, right: Int, top: Int, baseline: Int, bottom: Int,
    charSequence: CharSequence,
    start: Int, end: Int, lineNum: Int
  ) {
    val total = if (color.size > 5) 5 else color.size
    var leftMost = (total - 1) * -10
    for (i in 0 until total) {
      val oldColor = paint.color
      if (color[i] != 0) {
        paint.color = color[i]
      }
      canvas.drawCircle(
        ((left + right) / 2 - leftMost).toFloat(),
        bottom + radius,
        radius,
        paint
      )
      paint.color = oldColor
      leftMost += 20
    }
  }
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
