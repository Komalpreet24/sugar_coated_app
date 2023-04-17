package com.komal.sugarcoated.addNewRecord.ui.vm

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.komal.sugarcoated.addNewRecord.model.RecordDataModel
import com.komal.sugarcoated.network.NetworkResult
import com.komal.sugarcoated.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RecordViewModel(app: Application): AndroidViewModel(app) {

  private var  _auth: FirebaseAuth? = null
  private var  _db: FirebaseFirestore? = null
  private val  _saveRecordStatus  = MutableLiveData<NetworkResult.ResultOf<String>>()
  val saveRecordStatus: LiveData<NetworkResult.ResultOf<String>> = _saveRecordStatus

  init {
    _auth = FirebaseAuth.getInstance()
    _db = FirebaseFirestore.getInstance()
  }

  fun saveRecord(recordData: RecordDataModel){
    _saveRecordStatus.value = NetworkResult.ResultOf.Loading
    viewModelScope.launch(Dispatchers.IO){
      try{

        _auth?.uid?.let {
          _db?.collection("users")?.document(it)
            ?.update("recordList", FieldValue.arrayUnion(recordData))
            ?.addOnSuccessListener {
              Log.d(Constants.SAVE_RECORD, "Added new record with ID ${_auth?.uid}")
              _saveRecordStatus.postValue(
                NetworkResult.ResultOf.Success(Constants.SAVE_RECORD_SUCCESS))
            }
            ?.addOnFailureListener { exception ->
              Log.w(Constants.SAVE_RECORD, "Error adding new record $exception")
              _saveRecordStatus.postValue(
                NetworkResult.ResultOf.Failure(
                  "Error adding new record", exception))
            }
        }

      }catch (e:Exception){
        e.printStackTrace()
        _saveRecordStatus.postValue(NetworkResult.ResultOf.Failure("${e.message} ", e))
      }
    }
  }

  fun resetLoginLiveData(){
    _saveRecordStatus.value =  NetworkResult.ResultOf.Success(Constants.RESET)
  }

}