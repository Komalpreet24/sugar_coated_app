package com.komal.sugarcoated.home.ui.vm

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.AndroidViewModel
import androidx.preference.PreferenceManager


class HomeViewModel(app: Application): AndroidViewModel(app) {
    private val prefs: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(app)

        fun getTomatoSharedUrl(): String? {
        return prefs.getString("web-view link", null)

    }

}