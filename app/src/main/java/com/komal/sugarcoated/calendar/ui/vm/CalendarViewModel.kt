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
import com.komal.sugarcoated.signup.model.ChangeDates
import com.komal.sugarcoated.utils.AppSharedPreferences
import com.komal.sugarcoated.utils.Constants.EVENT_LOG
import com.komal.sugarcoated.utils.Constants.INFUSION_SET_CHANGE
import com.komal.sugarcoated.utils.Constants.LAST_CHANGED_DATE
import com.komal.sugarcoated.utils.Constants.RESET
import com.komal.sugarcoated.utils.Constants.SENSOR_CHANGE
import com.komal.sugarcoated.utils.EventDecorator
import java.util.*
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.text.SimpleDateFormat
import kotlin.collections.HashSet

class CalendarViewModel(app: Application): AndroidViewModel(app) {

  private var  _auth: FirebaseAuth? = null
  private var  _db: FirebaseFirestore? = null
  private val  _logEventStatus  = MutableLiveData<NetworkResult.ResultOf<String>?>()
  val logEventStatus: LiveData<NetworkResult.ResultOf<String>?> = _logEventStatus
  private val  _lastChangedStatus  = MutableLiveData<NetworkResult.ResultOf<ChangeDates>?>()
  val lastChangedStatus: LiveData<NetworkResult.ResultOf<ChangeDates>?> = _lastChangedStatus
  private var sharedPreferences: AppSharedPreferences? = null

  init {
    _auth = FirebaseAuth.getInstance()
    _db = FirebaseFirestore.getInstance()
    sharedPreferences = AppSharedPreferences.getInstance(app)
  }

    private var sensorChangeDates = hashSetOf<CalendarDay>()
    private var infusionSetChangeDates = hashSetOf<CalendarDay>()
    private var overlappingEventDates = hashSetOf<CalendarDay>()

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

    fun getOverlappingMarker(): EventDecorator {
      return EventDecorator(overlappingEventDates, overlappingEventColors)
    }

    fun getSensorMarker(): EventDecorator {
      return EventDecorator(sensorChangeDates, sensorChangeColors)

    }

    fun getInfusionSetMarker(): EventDecorator {
      return EventDecorator(infusionSetChangeDates, infusionSetChangeColors)
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

    private fun calendarDayToDate(calendarDay: CalendarDay): Date? {

        val calendar = Calendar.getInstance()
        calendar.set(calendarDay.year, calendarDay.month - 1, calendarDay.day)
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateString = dateFormatter.format(calendar.time)
        return dateFormatter.parse(dateString)

    }

    fun getLastChangedDate(){

      Log.d(LAST_CHANGED_DATE, "Fetching event for date, customerId = ${_auth?.uid}")
      _lastChangedStatus.value = NetworkResult.ResultOf.Loading
      _auth?.uid?.let {
        _db?.collection("users")?.document(it)?.get()
          ?.addOnSuccessListener { document ->

            val sensorChangeDate = document.getDate(SENSOR_CHANGE)
            val infusionSetChangeDate = document.getDate(INFUSION_SET_CHANGE)

            if (sensorChangeDate != null) {
              sensorChangeDates = getDatesWithFrequency(sensorChangeDate, 14)
            }

            if (infusionSetChangeDate != null) {
              infusionSetChangeDates = getDatesWithFrequency(infusionSetChangeDate, 4)
            }

            overlappingEventDates = infusionSetChangeDates.intersect(sensorChangeDates).toHashSet()
            sensorChangeDates.removeAll(overlappingEventDates)
            infusionSetChangeDates.removeAll(overlappingEventDates)

            _lastChangedStatus.postValue(
              NetworkResult.ResultOf.Success(ChangeDates(sensorChangeDate, infusionSetChangeDate)))
            Log.d(LAST_CHANGED_DATE, "Fetching last changed for user ${_auth?.uid}")

          }
          ?.addOnFailureListener { exception ->
            _lastChangedStatus.postValue(
              NetworkResult.ResultOf.Failure(
                "$exception", exception
              ))
            Log.w(LAST_CHANGED_DATE, "Error updating document $exception")
          }
      }

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