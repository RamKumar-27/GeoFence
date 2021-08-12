package com.cohesiontask.app

import android.app.Application
import com.cohesiontask.data.preferences.AppPreference

/*
*application Class
* */
class AppController : Application() {
    companion object {
        var instance: AppController? = null
    }

    private var preferences: AppPreference? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        preferences = AppPreference(this)
    }

    fun getAppPreferences(): AppPreference? {
        if (preferences != null)
            return preferences
        preferences = AppPreference(this)
        return preferences
    }
}