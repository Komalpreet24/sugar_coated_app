package com.komal.sugarcoated.home.ui.vm

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData




class HomeViewModel(app: Application): AndroidViewModel(app) {

    fun getTomatoSharedUrl(): String {
        return "https://global.awxbio.cool/remote/glycemic/57699.htm?openid=4459248874127953";
    }

}