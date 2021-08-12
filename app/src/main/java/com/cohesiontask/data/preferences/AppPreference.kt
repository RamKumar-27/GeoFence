package com.cohesiontask.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.cohesiontask.BuildConfig

class AppPreference constructor(context: Context) {

    private val preferences = context.getSharedPreferences(
        BuildConfig.APPLICATION_ID,
        Context.MODE_PRIVATE
    )

    fun clearAppPreference() {
        preferences.edit().clear().apply()
    }

}