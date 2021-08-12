package com.password.data.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cohesiontask.data.preferences.AppPreference

/*
* Base Class for All view models
*
* Here we can declare all the Common variables and methods
* which is used of all the viewModels
* */

abstract class BaseViewModel<T> : ViewModel() {

    /*preference class*/
    var appPreference: AppPreference? = null

    /*progress loading status*/
    val loadingStatus = MutableLiveData<Boolean>()
}