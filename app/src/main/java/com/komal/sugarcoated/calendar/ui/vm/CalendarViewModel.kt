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
import com.komal.sugarcoated.utils.Constants.EVENT_LOG
import com.komal.sugarcoated.utils.EventDecorator
import java.util.*
import com.prolificinteractive.materialcalendarview.CalendarDay

class CalendarViewModel(app: Application): AndroidViewModel(app) {

  private var  _auth: FirebaseAuth? = null
  private var  _db: FirebaseFirestore? = null
  private val  _logEventStatus  = MutableLiveData<NetworkResult.ResultOf<String>?>()
  val logEventStatus: LiveData<NetworkResult.ResultOf<String>?> = _logEventStatus

  init {
    _auth = FirebaseAuth.getInstance()
    _db = FirebaseFirestore.getInstance()
  }

  private val overlappingDates = hashSetOf(
      CalendarDay.from(2023, 4, 22),
      CalendarDay.from(2023, 4, 30),
      CalendarDay.from(2023, 5, 8)
    )

    private val sensorDates = hashSetOf(
      CalendarDay.from(2023, 4, 21),
      CalendarDay.from(2023, 4, 4)
    )

    private val pumpDates = hashSetOf(
      CalendarDay.from(2023, 4, 23),
      CalendarDay.from(2023, 4, 6)
    )

    private val eventMarkerColors = intArrayOf(
      Color.rgb(255, 0, 0),
      Color.rgb(0, 0, 255)
    )

    private val sensorColor = intArrayOf(
      Color.rgb(255, 0, 0),
    )

    private val pumpColor = intArrayOf(
      Color.rgb(0, 0, 255)
    )

    fun getCombinedMarker(): EventDecorator {
      return EventDecorator(overlappingDates, eventMarkerColors)
    }

    fun getSensorMarker(): EventDecorator {
      return EventDecorator(sensorDates, sensorColor)
    }

    fun getPumpMarker(): EventDecorator {
      return EventDecorator(pumpDates, pumpColor)
    }

    fun handleDateSelection(date: CalendarDay, eventMarker: String) {

        logEvent(date, eventMarker)

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

    private fun calendarDayToDate(calendarDay: CalendarDay): Date {
      val calendar = Calendar.getInstance()
      calendar.set(calendarDay.year, calendarDay.month - 1, calendarDay.day)
      return calendar.time
    }

}