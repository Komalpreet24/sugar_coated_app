package com.komal.sugarcoated.calendar.ui.fragment

import android.app.AlertDialog
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
import com.komal.sugarcoated.home.ui.vm.HomeActivityViewModel
import com.komal.sugarcoated.network.NetworkResult
import com.komal.sugarcoated.signup.model.UserSignUpData
import com.komal.sugarcoated.utils.*
import com.komal.sugarcoated.utils.Constants.INFUSION_SET_CHANGE
import com.komal.sugarcoated.utils.Constants.SAVE_SUPPLIES_SUCCESS
import com.komal.sugarcoated.utils.Constants.SENSOR_CHANGE
import com.mcdev.quantitizerlibrary.AnimationStyle
import com.mcdev.quantitizerlibrary.HorizontalQuantitizer
import com.prolificinteractive.materialcalendarview.*
import com.prolificinteractive.materialcalendarview.MaterialCalendarView.*
import java.util.*


class CalendarFragment : BaseFragment<FragmentCalendarBinding>(FragmentCalendarBinding::inflate) {

  private val calendarViewModel by viewModels<CalendarViewModel>()
  private val sharedViewModel by viewModels<HomeActivityViewModel>()

  private lateinit var markEventDialogView: View
  private lateinit var addSuppliesDialogView: View
  private lateinit var markEventDialogBuilder: AlertDialog.Builder
  private lateinit var addSuppliesDialogBuilder: AlertDialog.Builder
  private lateinit var markEventDialog: AlertDialog
  private lateinit var addSuppliesDialog: AlertDialog
  private lateinit var btnSensorChange: Button
  private lateinit var btnInfusionSetChange: Button
  private lateinit var ivTickSensor: ImageView
  private lateinit var ivInfusionSet: ImageView
  private lateinit var tvSensorChange: TextView
  private lateinit var tvInfusionSetChange: TextView
  private lateinit var etSensorSupplies: HorizontalQuantitizer
  private lateinit var etInfusionSetSupplies: HorizontalQuantitizer
  private lateinit var etInsulinSupplies: HorizontalQuantitizer
  private lateinit var btnSave: Button
  private lateinit var calendarView: MaterialCalendarView

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    setUpDialogs()
    setUpCalendar()
    observeLogEvent()
    observeCalendarData()
    observeSaveSupplies()
    sharedViewModel.getUserData()
    setUpClickListeners()

  }

  private fun setUpDialogs() {

    markEventDialogView = LayoutInflater.from(requireContext())
      .inflate(R.layout.log_event_dialog_box, null)
    markEventDialogBuilder = AlertDialog.Builder(requireContext())
    btnSensorChange = markEventDialogView.findViewById(R.id.btn_sensor_change)
    ivTickSensor = markEventDialogView.findViewById(R.id.iv_tick_sensor)
    ivInfusionSet = markEventDialogView.findViewById(R.id.iv_tick_infusion_set)
    tvSensorChange = markEventDialogView.findViewById(R.id.tv_sensor_change)
    tvInfusionSetChange = markEventDialogView.findViewById(R.id.tv_infusion_set)
    btnInfusionSetChange = markEventDialogView.findViewById(R.id.btn_infusion_change)
    addSuppliesDialogView = LayoutInflater.from(requireContext())
      .inflate(R.layout.add_supplies_dialog, null)
    addSuppliesDialogBuilder = AlertDialog.Builder(requireContext())
    etSensorSupplies = addSuppliesDialogView.findViewById(R.id.et_sensor_supplies)
    etInfusionSetSupplies = addSuppliesDialogView.findViewById(R.id.et_infusion_set_supplies)
    etInsulinSupplies = addSuppliesDialogView.findViewById(R.id.et_insulin_supplies)
    btnSave = addSuppliesDialogView.findViewById(R.id.btn_save)

    val markEventParent = markEventDialogView.parent as? ViewGroup
    markEventParent?.removeView(markEventDialogView)
    markEventDialogBuilder.setTitle("Mark an event")
    markEventDialogBuilder.setView(markEventDialogView)
    markEventDialog = markEventDialogBuilder.create()

    val addSuppliesParent = addSuppliesDialogView.parent as? ViewGroup
    addSuppliesParent?.removeView(addSuppliesDialogView)
    addSuppliesDialogBuilder.setView(addSuppliesDialogView)
    addSuppliesDialog = addSuppliesDialogBuilder.create()

    setDesignForInputs(arrayOf(etSensorSupplies, etInfusionSetSupplies, etInsulinSupplies))

  }

  private fun setUpClickListeners(){
    binding.btnAddSupplies.setOnClickListener{
      showAddSuppliesDialog()
    }

    btnSave.setOnClickListener{
      calendarViewModel.saveSupplies( etSensorSupplies.value,
                                      etInfusionSetSupplies.value,
                                      etInsulinSupplies.value)
    }

  }

  private fun setDesignForInputs(array: Array<HorizontalQuantitizer>){
    for(view in array){
      view.textAnimationStyle = AnimationStyle.FALL_IN
      view.setMinusIconBackgroundColor(R.color.BeigeLight)
      view.setPlusIconBackgroundColor(R.color.BeigeLight)
      view.setMinusIconColor(R.color.GreenMain)
      view.setPlusIconColor(R.color.GreenDark)
    }
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

  private fun observeCalendarData() {
    sharedViewModel.userDataStatus.observe(viewLifecycleOwner) { result ->
      result?.let {
        when (it) {
          is NetworkResult.ResultOf.Success -> {
            hideProgress()

            val userData: UserSignUpData = it.value
              calendarView.addDecorator(
                calendarViewModel.getSensorMarker(userData.sensorChangeDate))
              calendarView.addDecorator(
                calendarViewModel.getInfusionSetMarker(userData.infusionSetChangeDate))
              calendarView.addDecorator(
                calendarViewModel.getOverlappingMarker( userData.sensorChangeDate,
                                                        userData.infusionSetChangeDate))

              if(userData.sensorChangeDate === Date())
                calendarViewModel.saveEventChangeDates(SENSOR_CHANGE)
              if(userData.infusionSetChangeDate === Date())
                calendarViewModel.saveEventChangeDates(INFUSION_SET_CHANGE)

              etSensorSupplies.value      = userData.sensorSupplies
              etInsulinSupplies.value     = userData.insulinSupplies
              etInfusionSetSupplies.value = userData.infusionSetSupplies

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

  private fun observeSaveSupplies() {
    calendarViewModel.saveSuppliesStatus.observe(viewLifecycleOwner) { result ->
      result?.let {
        when (it) {
          is NetworkResult.ResultOf.Success -> {
            hideProgress()
            if (it.value.equals(SAVE_SUPPLIES_SUCCESS, ignoreCase = true)) {
              addSuppliesDialog.dismiss()
              showToast(
                requireContext(),
                String.format(getString(R.string.save_supplies))
              )
            }
          }
          is NetworkResult.ResultOf.Failure -> {
            hideProgress()
            addSuppliesDialog.dismiss()
            val failedMessage = it.message ?: getString(R.string.unknown_error)
            showToast(
              requireContext(),
              String.format(getString(R.string.save_supplies_saves), failedMessage)
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

    calendarView.setOnDateChangedListener { widget, date, _ ->


      if (date == CalendarDay.today()) {

        showEventMarkerDialog()

        if (calendarViewModel.isInfusionSetChangedToday() == true &&
          calendarViewModel.isSensorChangedToday() == true) {
          sensorChangeDone()
          infusionSetChangeDone()
        }else if (calendarViewModel.isSensorChangedToday() == true) {
          sensorChangeDone()
        }else if (calendarViewModel.isInfusionSetChangedToday() == true) {
          infusionSetChangeDone()
        }

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

  private fun showEventMarkerDialog(){
    markEventDialog.show()
    sharedViewModel.getUserData()
  }

  private fun showAddSuppliesDialog(){
    addSuppliesDialog.show()
    sharedViewModel.getUserData()
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

