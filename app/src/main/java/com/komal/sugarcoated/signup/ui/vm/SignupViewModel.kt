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
import com.komal.sugarcoated.network.NetworkResult
import com.komal.sugarcoated.network.NetworkResult.ResultOf
import com.komal.sugarcoated.utils.Constants
import com.komal.sugarcoated.utils.Constants.SIGNUP
import com.komal.sugarcoated.utils.Constants.SIGNUP_SUCCESS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignupViewModel(app: Application): AndroidViewModel(app) {

  private var  _auth: FirebaseAuth? = null
  private val  _signUpStatus  = MutableLiveData<ResultOf<String>>()

  init {
    _auth = FirebaseAuth.getInstance()
  }

  val signUpStatus: LiveData<ResultOf<String>> = _signUpStatus

  fun signUp(email:String, password:String){
    _signUpStatus.value = ResultOf.Loading
    viewModelScope.launch(Dispatchers.IO){
      try{
        _auth?.let { authentication ->
          authentication.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {task: Task<AuthResult> ->
              if(task.isSuccessful){
                Log.i(SIGNUP, "Registration Successful")
                _signUpStatus.postValue(ResultOf.Success(SIGNUP_SUCCESS))
              }else{
                Log.d(SIGNUP, "Registration Failed with ${task.exception}")
                _signUpStatus.postValue(
                  ResultOf.Failure(
                    "${task.exception}", task.exception))
              }
            }
        }
      }catch (e:Exception){
        e.printStackTrace()
        _signUpStatus.postValue(ResultOf.Failure("${e.message} ", e))
      }
    }
  }

  fun resetSignUpLiveData(){
    _signUpStatus.value =  ResultOf.Success(Constants.RESET)
  }

}
