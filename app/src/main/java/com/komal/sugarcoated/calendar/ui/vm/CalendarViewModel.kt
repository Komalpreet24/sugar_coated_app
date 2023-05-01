package com.komal.sugarcoated.calendar.ui.vm

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.komal.sugarcoated.network.NetworkResult
import com.komal.sugarcoated.utils.*
import com.komal.sugarcoated.utils.Constants.EVENT_LOG
import com.komal.sugarcoated.utils.Constants.INFUSION_SET_CHANGE
import com.komal.sugarcoated.utils.Constants.INFUSION_SET_SUPPLIES
import com.komal.sugarcoated.utils.Constants.INSULIN_SUPPLIES
import com.komal.sugarcoated.utils.Constants.MONTHS
import com.komal.sugarcoated.utils.Constants.SAVE_SUPPLIES
import com.komal.sugarcoated.utils.Constants.SAVE_SUPPLIES_SUCCESS
import com.komal.sugarcoated.utils.Constants.SENSOR_CHANGE
import com.komal.sugarcoated.utils.Constants.SENSOR_SUPPLIES
import com.komal.sugarcoated.utils.Constants.WEEKS
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class CalendarViewModel(app: Application): AndroidViewModel(app) {

  private var  _auth: FirebaseAuth? = null
  private var  _db: FirebaseFirestore? = null
  private val  _logEventStatus  = MutableLiveData<NetworkResult.ResultOf<String>?>()
  val logEventStatus: LiveData<NetworkResult.ResultOf<String>?> = _logEventStatus
  private val  _saveSuppliesStatus  = MutableLiveData<NetworkResult.ResultOf<String>?>()
  val saveSuppliesStatus: LiveData<NetworkResult.ResultOf<String>?> = _saveSuppliesStatus
  private var sensorChangeDates: HashSet<CalendarDay> = hashSetOf()
  private var infusionSetChangeDates: HashSet<CalendarDay> = hashSetOf()
  private var sharedPreferences: AppSharedPreferences? = null


  init {
    _auth = FirebaseAuth.getInstance()
    _db = FirebaseFirestore.getInstance()
    sharedPreferences = AppSharedPreferences.getInstance(app)
  }

  fun getOverlappingMarker(sensorChangeDate: Date, infusionSetChangeDate: Date): EventDecorator {

    sensorChangeDates = getDatesWithFrequency(sensorChangeDate, 14)
    infusionSetChangeDates = getDatesWithFrequency(infusionSetChangeDate, 4)

    val overlappingEventDates: HashSet<CalendarDay> =
      infusionSetChangeDates.intersect(sensorChangeDates).toHashSet()
    sensorChangeDates.removeAll(overlappingEventDates)
    infusionSetChangeDates.removeAll(overlappingEventDates)

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
            }.addOnFailureListener { exception ->
              _saveSuppliesStatus.postValue(
                NetworkResult.ResultOf.Failure("$exception", exception))
              Log.w(SAVE_SUPPLIES, "Error updating document", exception)
            }
        }
      }?.addOnFailureListener { exception ->
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

  fun calculateSupplyDuration(lastChangedDate: Date, repetition: Int, totalSupplies: Int): String {

    val currentDate = Date()
    val daysSinceLastChanged
            = TimeUnit.MILLISECONDS.toDays(currentDate.time - lastChangedDate.time).toInt()
    val daysPerSupply = repetition * totalSupplies
    val suppliesRemaining
            = kotlin.math.ceil((daysPerSupply - daysSinceLastChanged) / daysPerSupply.toDouble())
              .toInt()

    val daysRemaining = (suppliesRemaining * daysPerSupply) - daysSinceLastChanged
    val weeksRemaining = daysRemaining / 7
    val monthsRemaining = daysRemaining / 30

    val duration = if (monthsRemaining > 0) {
      Pair(monthsRemaining, MONTHS)
    } else {
      Pair(weeksRemaining, WEEKS)
    }

      return "${duration.first} ${duration.second}"

  }

  fun saveEventChangeDates(eventMarkedToday: String){
      sharedPreferences?.setTodayForSensorChange(eventMarkedToday == SENSOR_CHANGE)
      sharedPreferences?.setTodayForInfusionSetChange(eventMarkedToday == INFUSION_SET_CHANGE)
  }

  fun isInfusionSetChangedToday(): Boolean? {
    return sharedPreferences?.isInfusionSetChangedToday
  }

  fun isSensorChangedToday(): Boolean? {
    return sharedPreferences?.isSensorChangedToday
  }

}