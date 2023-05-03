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
import com.google.firebase.storage.FirebaseStorage
import com.komal.sugarcoated.network.NetworkResult
import com.komal.sugarcoated.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FilesViewModel(app: Application): AndroidViewModel(app) {

  private var _auth: FirebaseAuth? = null
  private var _db: FirebaseFirestore? = null
  private var _storage: FirebaseStorage? = null
  private val  _uploadFileStatus  = MutableLiveData<NetworkResult.ResultOf<String>?>()
  val uploadFileStatus: LiveData<NetworkResult.ResultOf<String>?> = _uploadFileStatus

  init {
    _auth = FirebaseAuth.getInstance()
    _db = FirebaseFirestore.getInstance()
    _storage = FirebaseStorage.getInstance()
  }

  fun uploadFile(fileUri: Uri) {

    val storageRef = _storage?.reference?.child("files/${fileUri.lastPathSegment}")
    val uploadTask = storageRef?.putFile(fileUri)

    _uploadFileStatus.value = NetworkResult.ResultOf.Loading
    uploadTask?.continueWithTask { task ->
      if (!task.isSuccessful) {
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
        saveFileDownloadUrlToFirestore(downloadUri)
      } else {
        _uploadFileStatus.postValue(
          NetworkResult.ResultOf.Failure(
            "Error uploading file", task.exception))
      }
    }

  }

  private fun saveFileDownloadUrlToFirestore(downloadUri: Uri) {

    val file = hashMapOf(
      "name" to "My File",
      "url" to downloadUri.toString()
    )
    viewModelScope.launch(Dispatchers.IO){
      try{

        _auth?.uid?.let {
          file["userId"] = it
          _db?.collection("files")?.document()
            ?.set(file)
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

}