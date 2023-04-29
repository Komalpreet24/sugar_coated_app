package com.komal.sugarcoated.calendar.ui.fragment

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.komal.sugarcoated.R
import com.komal.sugarcoated.calendar.ui.vm.CalendarViewModel
import com.komal.sugarcoated.databinding.FragmentCalendarBinding
import com.komal.sugarcoated.network.NetworkResult
import com.komal.sugarcoated.utils.*
import com.komal.sugarcoated.utils.Constants.INFUSION_SET_CHANGE
import com.komal.sugarcoated.utils.Constants.RESET
import com.komal.sugarcoated.utils.Constants.SENSOR_CHANGE
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.MaterialCalendarView.*


class CalendarFragment : BaseFragment<FragmentCalendarBinding>(FragmentCalendarBinding::inflate) {

  private val calendarViewModel by viewModels<CalendarViewModel>()

  private lateinit var dialogView: View
  private lateinit var dialogBuilder: AlertDialog.Builder
  private lateinit var dialog: AlertDialog
  private lateinit var btnSensorChange: Button
  private lateinit var btnInfusionSetChange: Button
  private lateinit var ivTickSensor: ImageView
  private lateinit var ivInfusionSet: ImageView
  private lateinit var tvSensorChange: TextView
  private lateinit var tvInfusionSetChange: TextView
  private lateinit var calendarView: MaterialCalendarView

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    initView()
    setUpCalendar()
    observeLogEvent()
    observeLastChangedEvent()
    calendarViewModel.getLastChangedDate()

  }

  private fun initView() {
    dialogView            = LayoutInflater.from(requireContext())
      .inflate(R.layout.log_event_dialog_box, null)
    dialogBuilder         = AlertDialog.Builder(requireContext())
    btnSensorChange       = dialogView.findViewById(R.id.btn_sensor_change)
    ivTickSensor          = dialogView.findViewById(R.id.iv_tick_sensor)
    ivInfusionSet         = dialogView.findViewById(R.id.iv_tick_infusion_set)
    tvSensorChange        = dialogView.findViewById(R.id.tv_sensor_change)
    tvInfusionSetChange   = dialogView.findViewById(R.id.tv_infusion_set)
    btnInfusionSetChange  = dialogView.findViewById(R.id.btn_infusion_change)
  }

  private fun observeLogEvent() {
    calendarViewModel.logEventStatus.observe(viewLifecycleOwner) { result ->
      result?.let {
        when (it) {
          is NetworkResult.ResultOf.Success -> {
            hideProgress()
            if (it.value.equals(SENSOR_CHANGE, ignoreCase = true)) {
              sensorChangeDone()
            } else if (it.value.equals(INFUSION_SET_CHANGE, ignoreCase = true)) {
              infusionSetChangeDone()
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

  private fun observeLastChangedEvent() {
    calendarViewModel.lastChangedStatus.observe(viewLifecycleOwner) { result ->
      result?.let {
        when (it) {
          is NetworkResult.ResultOf.Success -> {
            hideProgress()
            calendarView.addDecorator(calendarViewModel.getSensorMarker())
            calendarView.addDecorator(calendarViewModel.getInfusionSetMarker())
            calendarView.addDecorator(calendarViewModel.getOverlappingMarker())
          }
          is NetworkResult.ResultOf.Failure -> {
            hideProgress()
            val failedMessage = it.message ?: getString(R.string.unknown_error)
            showToast(
              requireContext(),
              String.format(getString(R.string.fetch_today_event), failedMessage)
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
    calendarView = binding.calendarView

    calendarView.selectionMode = SELECTION_MODE_SINGLE
    calendarView.selectedDate = CalendarDay.today()
    calendarViewModel.saveEventChangeDates(RESET)

    calendarView.setOnDateChangedListener { widget, date, _ ->


      if (date == CalendarDay.today()) {

        showDialog()
        if (calendarViewModel.isInfusionSetChangedToday() == true &&
          calendarViewModel.isSensorChangedToday() == true) {
          sensorChangeDone()
          infusionSetChangeDone()
        }else if (calendarViewModel.isSensorChangedToday() == true) {
          sensorChangeDone()
        }else if (calendarViewModel.isInfusionSetChangedToday() == true) {
          infusionSetChangeDone()
        }
        widget.selectionColor = Color.TRANSPARENT

        btnSensorChange.setOnClickListener{
          calendarViewModel.handleDateSelection(date, SENSOR_CHANGE)
        }
        btnInfusionSetChange.setOnClickListener{
          calendarViewModel.handleDateSelection(date, INFUSION_SET_CHANGE)
        }

      }else{

        calendarView.selectedDate = CalendarDay.today()

      }
    }
  }

  private fun showDialog(){
    val parent = dialogView.parent as? ViewGroup
    parent?.removeView(dialogView)
    dialogBuilder.setTitle("Mark an event")
    dialogBuilder.setView(dialogView)
    dialog = dialogBuilder.create()
    dialog.show()
  }

  private fun sensorChangeDone() {
    btnSensorChange.visibility  = INVISIBLE
    btnSensorChange.isEnabled   = false
    ivTickSensor.visibility     = VISIBLE
    tvSensorChange.visibility   = VISIBLE
    calendarViewModel.saveEventChangeDates(SENSOR_CHANGE)
  }

  private fun infusionSetChangeDone() {
    btnInfusionSetChange.visibility = INVISIBLE
    btnInfusionSetChange.isEnabled = false
    ivInfusionSet.visibility = VISIBLE
    tvInfusionSetChange.visibility = VISIBLE
    calendarViewModel.saveEventChangeDates(INFUSION_SET_CHANGE)
  }

}

