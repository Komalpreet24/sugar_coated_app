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

        _auth?.uid?.let { uid ->
          _db?.collection("blood_sugar_records")
            ?.whereEqualTo("userId", uid)
            ?.get()
            ?.addOnSuccessListener {documents ->
              val recordList = ArrayList<RecordDataModel>()
              for (document in documents) {
                val record = document.toObject(RecordDataModel::class.java)
                recordList.add(record)
              }
              _recordList.postValue(NetworkResult.ResultOf.Success(recordList))
              }
            }?.addOnFailureListener { exception ->
              _recordList.postValue(
                NetworkResult.ResultOf.Failure("${exception.message} ", exception))
            }

      }catch (e:Exception){
        e.printStackTrace()
        Log.e(Constants.FETCH_RECORDS, "getRecordList Failed with ${e.message}")
        _recordList.postValue(NetworkResult.ResultOf.Failure("${e.message} ", e))
      }
    }
  }

  fun resetRecordListData(){
    _recordList.value =  null
  }
}