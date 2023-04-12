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
import com.google.firebase.firestore.FirebaseFirestore
import com.komal.sugarcoated.network.NetworkResult.ResultOf
import com.komal.sugarcoated.signup.model.UserSignUpData
import com.komal.sugarcoated.utils.Constants
import com.komal.sugarcoated.utils.Constants.SIGNUP
import com.komal.sugarcoated.utils.Constants.SIGNUP_SUCCESS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SignupViewModel(app: Application): AndroidViewModel(app) {

  private var  _auth: FirebaseAuth? = null
  private var  _db: FirebaseFirestore? = null
  private val  _signUpStatus  = MutableLiveData<ResultOf<String>>()

  init {
    _auth = FirebaseAuth.getInstance()
    _db = FirebaseFirestore.getInstance()
  }

  val signUpStatus: LiveData<ResultOf<String>> = _signUpStatus

  fun signUp(email:String, password:String, name: String){
    _signUpStatus.value = ResultOf.Loading
    viewModelScope.launch(Dispatchers.IO){
      try{
        _auth?.let { authentication ->
          authentication.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {task: Task<AuthResult> ->
              if(task.isSuccessful){
                Log.i(SIGNUP, "Registration Successful")
                setUserData(name, email)
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

  private fun setUserData(name: String, email: String){

    Log.d(SIGNUP, "name = ${name}, email = ${email}, customerId = ${_auth?.uid}")

    _auth?.uid?.let {
      _db?.collection("users")?.document(it)
        ?.set(UserSignUpData(name = name, email = email, id = _auth?.uid))
        ?.addOnSuccessListener {
          Log.d(SIGNUP, "Added document with ID ${_auth?.uid}")
          _signUpStatus.postValue(ResultOf.Success(SIGNUP_SUCCESS))
        }
        ?.addOnFailureListener { exception ->
          Log.w(SIGNUP, "Error adding document $exception")
          _signUpStatus.postValue(
            ResultOf.Failure(
              "Error adding document", exception))
        }
    }

  }

}
