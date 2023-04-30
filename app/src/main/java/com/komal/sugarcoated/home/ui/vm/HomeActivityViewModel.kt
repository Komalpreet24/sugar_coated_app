package com.komal.sugarcoated.home.ui.vm

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.komal.sugarcoated.network.NetworkResult
import com.komal.sugarcoated.signup.model.UserSignUpData
import com.komal.sugarcoated.utils.AppSharedPreferences
import com.komal.sugarcoated.utils.Constants.USER_DATA

class HomeActivityViewModel (app: Application): AndroidViewModel(app) {

  private lateinit var fireAuthListener: FirebaseAuth.AuthStateListener
  private var _auth: FirebaseAuth? = null
  private var  _db: FirebaseFirestore? = null
  private var sharedPreferences: AppSharedPreferences? = null
  private val  _userDataStatus = MutableLiveData<NetworkResult.ResultOf<UserSignUpData>?>()
  val userDataStatus: LiveData<NetworkResult.ResultOf<UserSignUpData>?> = _userDataStatus

  init {
    _auth = FirebaseAuth.getInstance()
    _db = FirebaseFirestore.getInstance()
    sharedPreferences = AppSharedPreferences.getInstance(app)
  }

  fun checkUserAuthStatus(){
    try{
      fireAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user == null) {
          sharedPreferences?.setLoggedInStatus(false)
        } else {
          sharedPreferences?.setLoggedInStatus(true)
        }
      }

    }catch (e:Exception){
      e.printStackTrace()
      sharedPreferences?.setLoggedInStatus(false)
    }
  }

  fun addAuthListener(){
    _auth?.addAuthStateListener(fireAuthListener)
  }

  fun removeAuthListener(){
    fireAuthListener.let { _auth?.removeAuthStateListener(it) }
  }

  fun getUserData(){

    Log.d(USER_DATA, "Fetching user data, customerId = ${_auth?.uid}")
    _userDataStatus.value = NetworkResult.ResultOf.Loading
    _auth?.uid?.let {
      _db?.collection("users")?.document(it)?.get()
        ?.addOnSuccessListener { document ->

          val suppliesAndCalendarData: UserSignUpData =
            document.toObject(UserSignUpData::class.java) as UserSignUpData

          _userDataStatus.postValue(
            NetworkResult.ResultOf.Success(suppliesAndCalendarData))
          Log.d(USER_DATA, "Fetching user data for ${_auth?.uid}")

        }
        ?.addOnFailureListener { exception ->
          _userDataStatus.postValue(
            NetworkResult.ResultOf.Failure(
              "$exception", exception
            ))
          Log.w(USER_DATA, "Error updating document $exception")
        }
    }

  }

}