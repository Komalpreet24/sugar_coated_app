package com.komal.sugarcoated.home.ui.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.komal.sugarcoated.utils.AppSharedPreferences

class HomeActivityViewModel (app: Application): AndroidViewModel(app) {

  private lateinit var fireAuthListener: FirebaseAuth.AuthStateListener
  private var _auth: FirebaseAuth? = null
  private var sharedPreferences: AppSharedPreferences? = null

  init {
    _auth = FirebaseAuth.getInstance()
    sharedPreferences = AppSharedPreferences.getInstance(app)
  }

  fun checkUserAuthStatus(){
    try{
      fireAuthListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val user = firebaseAuth.currentUser
        if (user == null) {
          sharedPreferences?.setLoggedInStatus(false)
        } else {
          sharedPreferences?.setLoggedInStatus(true)
        }
      }

    }catch (e:Exception){
      e.printStackTrace()
      sharedPreferences?.setLoggedInStatus(false)
    }
  }

  fun addAuthListener(){
    _auth?.addAuthStateListener(fireAuthListener)
  }

  fun removeAuthListener(){
    fireAuthListener.let { _auth?.removeAuthStateListener(it) }
  }

}