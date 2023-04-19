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
import com.google.firebase.firestore.FirebaseFirestore
import com.komal.sugarcoated.network.NetworkResult.ResultOf
import com.komal.sugarcoated.utils.Constants.LOGIN
import com.komal.sugarcoated.utils.Constants.LOGIN_SUCCESS
import com.komal.sugarcoated.utils.Constants.LOGOUT_SUCCESS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class LoginViewModel(app: Application): AndroidViewModel(app) {

  private var  _auth: FirebaseAuth? = null
  private var  _db: FirebaseFirestore? = null
  private val  _loginStatus  = MutableLiveData<ResultOf<String>?>()
  private val _logoutStatus = MutableLiveData<ResultOf<String>>()
  val loginStatus: LiveData<ResultOf<String>?> = _loginStatus
  val logoutStatus: LiveData<ResultOf<String>> = _logoutStatus

  init {
    _auth = FirebaseAuth.getInstance()
    _db = FirebaseFirestore.getInstance()
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
                    "${task.exception}", task.exception
                  )
                )
              }
            }
        }

      }catch (e:Exception){
        e.printStackTrace()
        Log.e(LOGIN, "Login Failed with ${e.message}")
        _loginStatus.postValue(ResultOf.Failure("${e.message} ", e))
      }
    }
  }

  fun resetLoginLiveData(){
    _loginStatus.value =  null
  }

  fun logout(){
    _logoutStatus.value = ResultOf.Loading
    viewModelScope.launch(Dispatchers.IO){
      try{
        _auth?.let { authentication ->
          authentication.signOut()
          _logoutStatus.postValue(ResultOf.Success(LOGOUT_SUCCESS))
        }

      }catch (e:Exception){
        e.printStackTrace()
        _logoutStatus.postValue(ResultOf.Failure("${e.message}", e))
      }
    }
  }

  fun setUserData(newValue: Any?){

    Log.d(LOGIN, "web-view link = ${newValue}, customerId = ${_auth?.uid}")

    _auth?.uid?.let {
      _db?.collection("users")?.document(it)
        ?.update("webViewLink", newValue)
        ?.addOnSuccessListener {
          Log.d(LOGIN, "Web-view link ${newValue.toString()} updated for user ${_auth?.uid}")
        }
        ?.addOnFailureListener { exception ->
          Log.w(LOGIN, "Error updating document $exception")
        }
    }

  }

}
