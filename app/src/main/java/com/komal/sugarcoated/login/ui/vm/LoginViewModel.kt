package com.komal.sugarcoated.login.ui.vm

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
import com.komal.sugarcoated.utils.Constants.LOGIN
import com.komal.sugarcoated.utils.Constants.LOGIN_SUCCESS
import com.komal.sugarcoated.utils.Constants.RESET
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginViewModel(app: Application): AndroidViewModel(app) {

  private var  _auth: FirebaseAuth? = null
  private val  _loginStatus  = MutableLiveData<ResultOf<String>>()
  private val _logoutStatus = MutableLiveData<ResultOf<String>>()
  val loginStatus: LiveData<ResultOf<String>> = _loginStatus
  val logoutStatus: LiveData<ResultOf<String>> = _logoutStatus

  init {
    _auth = FirebaseAuth.getInstance()
  }

  fun signIn(email:String, password:String){
    _loginStatus.value = ResultOf.Loading
    viewModelScope.launch(Dispatchers.IO){
      try{
        _auth?.let{ login->
          login.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {task: Task<AuthResult> ->
              if(task.isSuccessful){
                Log.i(LOGIN, "Login Successful")
                _loginStatus.postValue(ResultOf.Success(LOGIN_SUCCESS))
              }else{
                Log.d(LOGIN, "Login Failed with ${task.exception}")
                _loginStatus.postValue(
                  ResultOf.Failure(
                    "Login Failed: ${task.exception}", task.exception))
              }
            }
        }

      }catch (e:Exception){
        e.printStackTrace()
        _loginStatus.postValue(ResultOf.Failure("Failed with Exception ${e.message} ", e))
      }
    }
  }

//  fun resetLoginLiveData(){
//    _loginStatus.value =  ResultOf.Success(RESET)
//  }
//
//  fun logout(){
//    _logoutStatus.value = ResultOf.Loading
//    viewModelScope.launch(Dispatchers.IO){
//      var  errorCode = -1
//      try{
//        _auth?.let {authentation ->
//          authentation.signOut()
//          _logoutStatus.postValue(ResultOf.Success("Signout Successful"))
//        }
//
//      }catch (e:Exception){
//        e.printStackTrace()
//        _logoutStatus.postValue(ResultOf.Failure("Failed with Exception ${e.message} ", e))
//      }
//    }
//  }

}
