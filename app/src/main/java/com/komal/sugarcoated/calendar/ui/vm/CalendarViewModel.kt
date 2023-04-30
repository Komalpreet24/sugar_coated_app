package com.komal.sugarcoated.calendar.ui.vm

import android.app.Application
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.komal.sugarcoated.network.NetworkResult
import com.komal.sugarcoated.utils.AppSharedPreferences
import com.komal.sugarcoated.utils.Constants.EVENT_LOG
import com.komal.sugarcoated.utils.Constants.INFUSION_SET_CHANGE
import com.komal.sugarcoated.utils.Constants.INFUSION_SET_SUPPLIES
import com.komal.sugarcoated.utils.Constants.INSULIN_SUPPLIES
import com.komal.sugarcoated.utils.Constants.RESET
import com.komal.sugarcoated.utils.Constants.SAVE_SUPPLIES
import com.komal.sugarcoated.utils.Constants.SAVE_SUPPLIES_SUCCESS
import com.komal.sugarcoated.utils.Constants.SENSOR_CHANGE
import com.komal.sugarcoated.utils.Constants.SENSOR_SUPPLIES
import com.komal.sugarcoated.utils.EventDecorator
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.text.SimpleDateFormat
import java.util.*

class CalendarViewModel(app: Application): AndroidViewModel(app) {

  private var  _auth: FirebaseAuth? = null
  private var  _db: FirebaseFirestore? = null
  private val  _logEventStatus  = MutableLiveData<NetworkResult.ResultOf<String>?>()
  val logEventStatus: LiveData<NetworkResult.ResultOf<String>?> = _logEventStatus
  private val  _saveSuppliesStatus  = MutableLiveData<NetworkResult.ResultOf<String>?>()
  val saveSuppliesStatus: LiveData<NetworkResult.ResultOf<String>?> = _saveSuppliesStatus
  private var sharedPreferences: AppSharedPreferences? = null


  init {
    _auth = FirebaseAuth.getInstance()
    _db = FirebaseFirestore.getInstance()
    sharedPreferences = AppSharedPreferences.getInstance(app)
  }

    private val overlappingEventColors = intArrayOf(
      Color.rgb(255, 0, 0),
      Color.rgb(0, 0, 255)
    )
    private val infusionSetChangeColors = intArrayOf(
      Color.rgb(255, 0, 0),
    )
    private val sensorChangeColors = intArrayOf(
      Color.rgb(0, 0, 255),
    )

    fun getOverlappingMarker(sensorChangeDate: Date, infusionSetChangeDate: Date): EventDecorator {

      val sensorChangeDates = getDatesWithFrequency(sensorChangeDate, 14)
      val infusionSetChangeDates = getDatesWithFrequency(infusionSetChangeDate, 4)

      val overlappingEventDates: HashSet<CalendarDay> =
        infusionSetChangeDates.intersect(sensorChangeDates).toHashSet()
      sensorChangeDates.removeAll(overlappingEventDates)
      infusionSetChangeDates.removeAll(overlappingEventDates)

      return EventDecorator(overlappingEventDates, overlappingEventColors)
    }

    fun getSensorMarker(sensorChangeDate: Date): EventDecorator {
      return EventDecorator(getDatesWithFrequency(sensorChangeDate, 14), sensorChangeColors)
    }

    fun getInfusionSetMarker(infusionSetChangeDate: Date): EventDecorator {
      return EventDecorator(getDatesWithFrequency(infusionSetChangeDate, 4), infusionSetChangeColors)
    }


    fun handleDateSelection(date: CalendarDay, eventMarker: String) {
        logEvent(date, eventMarker)
    }

    private fun getDatesWithFrequency(date: Date, repetition: Int): HashSet<CalendarDay> {
      val calendar = Calendar.getInstance()
      calendar.time = date
      val sixMonthsLater = Calendar.getInstance()
      sixMonthsLater.add(Calendar.MONTH, 12)
      val dates = HashSet<CalendarDay>()
      while (calendar.before(sixMonthsLater)) {
        dates.add(CalendarDay.from(calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH)))
        calendar.add(Calendar.DATE, repetition)
      }
      return dates
    }

    private fun logEvent(date: CalendarDay, event: String){

      Log.d(EVENT_LOG, "Event log date = ${date}, customerId = ${_auth?.uid}")
      _logEventStatus.value = NetworkResult.ResultOf.Loading
      _auth?.uid?.let {
        _db?.collection("users")?.document(it)
          ?.update(event, calendarDayToDate(date))
          ?.addOnSuccessListener {
            _logEventStatus.postValue(NetworkResult.ResultOf.Success(event))
            Log.d(EVENT_LOG, "Event log $date updated for user ${_auth?.uid}")
          }
          ?.addOnFailureListener { exception ->
            _logEventStatus.postValue(
              NetworkResult.ResultOf.Failure(
                "$exception", exception
              ))
            Log.w(EVENT_LOG, "Error updating document $exception")
          }
      }

    }

    fun saveSupplies(sensorSupplies: Int, infusionSetSupplies: Int, insulinSupplies: Int){

      Log.d(SAVE_SUPPLIES, "Save supplies $sensorSupplies, $infusionSetSupplies," +
                                " $insulinSupplies , customerId = ${_auth?.uid}")
      _saveSuppliesStatus.value = NetworkResult.ResultOf.Loading
      _auth?.uid?.let {

        val docRef = _db?.collection("users")?.document(it)

        docRef?.get()
          ?.addOnSuccessListener { document ->

            val existingSensorSupplies = document.get(SENSOR_SUPPLIES)
            val existingInfusionSetSupplies = document.get(INFUSION_SET_SUPPLIES)
            val existingInsulinSupplies = document.get(INSULIN_SUPPLIES)

            if (existingSensorSupplies != sensorSupplies ||
              existingInfusionSetSupplies != infusionSetSupplies ||
              existingInsulinSupplies != insulinSupplies
            ) {

              val updates = hashMapOf<String, Any>()
              if (existingSensorSupplies != sensorSupplies) {
                updates[SENSOR_SUPPLIES] = sensorSupplies
              }
              if (existingInfusionSetSupplies != infusionSetSupplies) {
                updates[INFUSION_SET_SUPPLIES] = infusionSetSupplies
              }
              if (existingInsulinSupplies != insulinSupplies) {
                updates[INSULIN_SUPPLIES] = insulinSupplies
              }

              docRef.update(updates)
                .addOnSuccessListener {
                  _saveSuppliesStatus.postValue(NetworkResult.ResultOf.Success(SAVE_SUPPLIES_SUCCESS))
                  Log.d(SAVE_SUPPLIES, "Save supplies updated for user ${_auth?.uid}")
                }
                .addOnFailureListener { exception ->
                  _saveSuppliesStatus.postValue(
                    NetworkResult.ResultOf.Failure("$exception", exception))
                  Log.w(SAVE_SUPPLIES, "Error updating document", exception)
                }

            }
          }
              ?.addOnFailureListener { exception ->
                _saveSuppliesStatus.postValue(
                  NetworkResult.ResultOf.Failure(
                    "$exception", exception
                  )
                )
                Log.w(SAVE_SUPPLIES, "Error updating document $exception")
              }
          }
    }

    private fun calendarDayToDate(calendarDay: CalendarDay): Date? {

        val calendar = Calendar.getInstance()
        calendar.set(calendarDay.year, calendarDay.month - 1, calendarDay.day)
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateString = dateFormatter.format(calendar.time)
        return dateFormatter.parse(dateString)

    }


  fun saveEventChangeDates(eventMarkedToday: String){
      sharedPreferences?.setTodayForSensorChange(eventMarkedToday == SENSOR_CHANGE)
      sharedPreferences?.setTodayForInfusionSetChange(eventMarkedToday == INFUSION_SET_CHANGE)
    if(eventMarkedToday == RESET){
      sharedPreferences?.setTodayForSensorChange(false)
      sharedPreferences?.setTodayForInfusionSetChange(false)
    }
  }

  fun isInfusionSetChangedToday(): Boolean? {
    return sharedPreferences?.isInfusionSetChangedToday
  }

  fun isSensorChangedToday(): Boolean? {
    return sharedPreferences?.isSensorChangedToday
  }
}