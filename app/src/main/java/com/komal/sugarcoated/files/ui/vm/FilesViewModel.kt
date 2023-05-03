package com.komal.sugarcoated.files.ui.vm

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.komal.sugarcoated.files.model.FileData
import com.komal.sugarcoated.network.NetworkResult
import com.komal.sugarcoated.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class FilesViewModel(app: Application): AndroidViewModel(app) {

  private var _auth: FirebaseAuth? = null
  private var _db: FirebaseFirestore? = null
  private var _storage: FirebaseStorage? = null
  private val  _uploadFileStatus  = MutableLiveData<NetworkResult.ResultOf<String>?>()
  val uploadFileStatus: LiveData<NetworkResult.ResultOf<String>?> = _uploadFileStatus
  private val  _fileList = MutableLiveData<NetworkResult.ResultOf<ArrayList<FileData>?>?>()
  val fileList: LiveData<NetworkResult.ResultOf<ArrayList<FileData>?>?> = _fileList
  private val _progressPercent = MutableLiveData<Int?>()

  init {
    _auth = FirebaseAuth.getInstance()
    _db = FirebaseFirestore.getInstance()
    _storage = FirebaseStorage.getInstance()
  }

  fun uploadFile(fileName: String, fileUri: Uri) {

    val storageRef = _storage?.reference?.child("files/${fileUri.lastPathSegment}")
    val uploadTask = storageRef?.putFile(fileUri)

    uploadTask?.continueWithTask { task ->
      if (!task.isSuccessful) {
        _progressPercent.value = null
        task.exception?.let {
          _uploadFileStatus.postValue(
            NetworkResult.ResultOf.Failure(
              "Error uploading file", task.exception))
        }
      }
      storageRef.downloadUrl
    }?.addOnCompleteListener { task ->
      if (task.isSuccessful) {
        val downloadUri = task.result
        saveFileDownloadUrlToFirestore(fileName, downloadUri)
      } else {
        _uploadFileStatus.postValue(
          NetworkResult.ResultOf.Failure(
            "Error uploading file", task.exception))
      }
      _progressPercent.value = null
    }

  }

  private fun saveFileDownloadUrlToFirestore(fileName: String,downloadUri: Uri) {

    viewModelScope.launch(Dispatchers.IO){
      try{

        _auth?.uid?.let {
          _db?.collection("files")?.document()
            ?.set(FileData(fileName, downloadUri.toString(), it))
            ?.addOnSuccessListener {
              Log.d(Constants.UPLOAD_FILE, "Uploaded new file for user ${_auth?.uid}")
              _uploadFileStatus.postValue(
                NetworkResult.ResultOf.Success(Constants.UPLOAD_FILE_SUCCESS))
            }
            ?.addOnFailureListener { exception ->
              Log.w(Constants.UPLOAD_FILE, "Error uploading file $exception")
              _uploadFileStatus.postValue(
                NetworkResult.ResultOf.Failure(
                  "Error uploading file", exception))
            }
        }

      }catch (e:Exception){
        e.printStackTrace()
        _uploadFileStatus.postValue(NetworkResult.ResultOf.Failure("${e.message} ", e))
      }
    }

  }

  fun getFileList(){
    _fileList.value = NetworkResult.ResultOf.Loading
    viewModelScope.launch(Dispatchers.IO){
      try{
        _auth?.uid?.let { uid ->
          _db?.collection("files")
            ?.whereEqualTo("userId", uid)
            ?.orderBy("dateAndTime", Query.Direction.DESCENDING)
            ?.get()
            ?.addOnSuccessListener {documents ->
              val fileList = ArrayList<FileData>()
              for (document in documents) {
                val files = document.toObject(FileData::class.java)
                fileList.add(files)
              }
              _fileList.postValue(NetworkResult.ResultOf.Success(fileList))
            }
        }?.addOnFailureListener { exception ->
          _fileList.postValue(
            NetworkResult.ResultOf.Failure("${exception.message} ", exception))
        }

      }catch (e:Exception){
        e.printStackTrace()
        Log.e(Constants.FETCH_RECORDS, "getFileList Failed with ${e.message}")
        _fileList.postValue(NetworkResult.ResultOf.Failure("${e.message} ", e))
      }
    }
  }

  fun resetFileListData(){
    _fileList.value = null
  }

  fun resetUploadFileData(){
    _uploadFileStatus.value = null
  }

}