package com.komal.sugarcoated.calendar.ui.fragment

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import com.komal.sugarcoated.R
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.komal.sugarcoated.calendar.ui.vm.CalendarViewModel
import com.komal.sugarcoated.databinding.FragmentCalendarBinding
import com.komal.sugarcoated.network.NetworkResult
import com.komal.sugarcoated.utils.*
import com.komal.sugarcoated.utils.Constants.INFUSION_SET_CHANGE
import com.komal.sugarcoated.utils.Constants.SENSOR_CHANGE
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.MaterialCalendarView.*


class CalendarFragment : BaseFragment<FragmentCalendarBinding>(FragmentCalendarBinding::inflate) {

  private val calendarViewModel by viewModels<CalendarViewModel>()
  private val dialogView: View              = LayoutInflater.from(context)
                                              .inflate(R.layout.log_event_dialog_box, null)
  private val dialogBuilder                 = AlertDialog.Builder(context)
  private val btnSensorChange: Button       = dialogView.findViewById(R.id.btn_sensor_change)
  private val btnInfusionSetChange: Button  = dialogView.findViewById(R.id.btn_infusion_change)
  private val ivTickSensor: ImageView       = dialogView.findViewById(R.id.iv_tick_sensor)
  private val ivInfusionSet: ImageView      = dialogView.findViewById(R.id.iv_tick_infusion_set)
  private val tvSensorChange: TextView      = dialogView.findViewById(R.id.tv_sensor_change)
  private val tvInfusionSetChange: TextView = dialogView.findViewById(R.id.tv_infusion_set)

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    setUpCalendar()
    observeLogEvent()

  }

  private fun observeLogEvent() {
    calendarViewModel.logEventStatus.observe(viewLifecycleOwner) { result ->
      result?.let {
        when (it) {
          is NetworkResult.ResultOf.Success -> {
            hideProgress()
            if (it.value.equals(SENSOR_CHANGE, ignoreCase = true)) {
              btnSensorChange.visibility = INVISIBLE
              btnSensorChange.isEnabled = false
              ivTickSensor.visibility = VISIBLE
              tvSensorChange.visibility = VISIBLE
            } else if (!it.value.equals(INFUSION_SET_CHANGE, ignoreCase = true)) {
              btnInfusionSetChange.visibility = INVISIBLE
              btnInfusionSetChange.isEnabled = false
              ivInfusionSet.visibility = VISIBLE
              tvInfusionSetChange.visibility = VISIBLE
            }
          }
          is NetworkResult.ResultOf.Failure -> {
            hideProgress()
            val failedMessage = it.message ?: getString(R.string.unknown_error)
            showToast(
              requireContext(),
              String.format(getString(R.string.logging_event), failedMessage)
            )
          }
          is NetworkResult.ResultOf.Loading -> {
            showProgress(requireContext())
          }
        }
      }
    }
  }

  private fun setUpCalendar(){
    val calendarView: MaterialCalendarView = binding.calendarView

    calendarView.selectionMode = SELECTION_MODE_SINGLE
    calendarView.selectedDate = CalendarDay.today()

    calendarView.addDecorator(calendarViewModel.getCombinedMarker())
    calendarView.addDecorator(calendarViewModel.getSensorMarker())
    calendarView.addDecorator(calendarViewModel.getPumpMarker())

    calendarView.setOnDateChangedListener { widget, date, _ ->

      if (date == CalendarDay.today()) {

        dialogBuilder.setTitle("Mark an event")
        dialogBuilder.setView(dialogView)
        btnSensorChange.setOnClickListener{
          calendarViewModel.handleDateSelection(date, SENSOR_CHANGE)
        }
        btnInfusionSetChange.setOnClickListener{
          calendarViewModel.handleDateSelection(date, INFUSION_SET_CHANGE)
        }
        val dialog = dialogBuilder.create()
        dialog.show()
        widget.selectionColor = Color.TRANSPARENT

      }else{

        calendarView.selectedDate = CalendarDay.today()

      }
    }
  }
}

