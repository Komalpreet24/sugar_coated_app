package com.komal.sugarcoated.signup.ui.vm

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.komal.sugarcoated.network.NetworkResult.ResultOf
import com.komal.sugarcoated.utils.Constants.SIGNUP
import com.komal.sugarcoated.utils.Constants.SIGNUP_SUCCESS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignupViewModel(app: Application): AndroidViewModel(app) {

  private var  _auth: FirebaseAuth? = null
  private val  _registrationStatus  = MutableLiveData<ResultOf<String>>()

  init {
    _auth = FirebaseAuth.getInstance()
  }

  val registrationStatus: LiveData<ResultOf<String>> = _registrationStatus

  fun signUp(email:String, password:String){
    _registrationStatus.value = ResultOf.Loading
    viewModelScope.launch(Dispatchers.IO){
      try{
        _auth?.let { authentication ->
          authentication.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {task: Task<AuthResult> ->
              if(task.isSuccessful){
                Log.i(SIGNUP, "Registration Successful")
                _registrationStatus.postValue(ResultOf.Success(SIGNUP_SUCCESS))
              }else{
                Log.d(SIGNUP, "Registration Failed with ${task.exception}")
                _registrationStatus.postValue(
                  ResultOf.Failure(
                    "Registration Failed: ${task.exception}", task.exception))
              }
            }
        }
      }catch (e:Exception){
        e.printStackTrace()
        _registrationStatus.postValue(ResultOf.Failure("Failed with Exception ${e.message} ", e))
      }
    }
  }

}
