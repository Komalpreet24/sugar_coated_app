package com.komal.sugarcoated.logBook.ui.vm

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.komal.sugarcoated.addNewRecord.model.RecordDataModel
import com.komal.sugarcoated.network.NetworkResult
import com.komal.sugarcoated.signup.model.UserSignUpData
import com.komal.sugarcoated.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LogBookViewModel(app: Application): AndroidViewModel(app) {
  private var  _auth: FirebaseAuth? = null
  private var  _db: FirebaseFirestore? = null
  private val  _recordList = MutableLiveData<NetworkResult.ResultOf<ArrayList<RecordDataModel>?>?>()
  val recordList: LiveData<NetworkResult.ResultOf<ArrayList<RecordDataModel>?>?> = _recordList

  init {
    _auth = FirebaseAuth.getInstance()
    _db = FirebaseFirestore.getInstance()
  }

  fun getRecordList(){
    _recordList.value = NetworkResult.ResultOf.Loading
    viewModelScope.launch(Dispatchers.IO){
      try{

          _auth?.uid?.let {
            _db?.collection("users")?.document(it)?.get()
              ?.addOnSuccessListener { document ->
              if (document.exists()) {
                val user = document.toObject(UserSignUpData::class.java)
                val records: ArrayList<RecordDataModel>? = user?.recordList
                _recordList.postValue(NetworkResult.ResultOf.Success(records))
              }
            }?.addOnFailureListener { exception ->
                _recordList.postValue(
                  NetworkResult.ResultOf.Failure("${exception.message} ", exception))
            }
          }

      }catch (e:Exception){
        e.printStackTrace()
        Log.e(Constants.LOGIN, "Login Failed with ${e.message}")
        _recordList.postValue(NetworkResult.ResultOf.Failure("${e.message} ", e))
      }
    }
  }

  fun resetRecordListData(){
    _recordList.value =  null
  }
}