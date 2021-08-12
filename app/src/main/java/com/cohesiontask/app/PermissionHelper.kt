package com.cohesiontask.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment

/*
* Common permission enable class
* */
class PermissionHelper : ActivityCompat.OnRequestPermissionsResultCallback {

    private var requestCode: Int = 0
    private var fragment: Fragment? = null
    private var activity: AppCompatActivity? = null
    private var context: Context? = null
    private var listener: PermissionListener? = null

    private val REQUEST_CODE_LOCATION = 131

    private val LOCATION_PERMISSION_ARRAY =
        arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION)

    enum class PermissionType {
        LOCATION,
    }

    interface PermissionListener {
        fun onPermissionGranted()

        fun onPermissionDenied()
    }

    private fun getContext(): Context? {
        if (activity != null) {
            return activity
        } else if (fragment != null) {
            return fragment!!.context
        }
        return null
    }

    /**
     *
     * @param fragment    context of the fragment
     */
    constructor(fragment: Fragment) {
        this.fragment = fragment
        context = getContext()
    }


    /**
     *
     * @param activity    context of the activity
     */
    constructor(activity: AppCompatActivity) {
        this.activity = activity
        context = getContext()
    }


    /** it will get permission array based on the type
     *
     * @param requestCode
     *
     **/

    private fun getPermissionArray(requestCode: PermissionType): Array<String>? {
        when (requestCode) {
            PermissionType.LOCATION -> {
                this.requestCode = REQUEST_CODE_LOCATION
                return LOCATION_PERMISSION_ARRAY
            }
        }
    }

    /**
     * this method is check wether the given permission is Granted or not
     * */

    fun isPermissionGranted(permissionType: PermissionType): Boolean {
        val permissionArray = getPermissionArray(permissionType)
        var allPermissionsGranted = true
        var i = 0
        val mPermissionLength = permissionArray!!.size
        while (i < mPermissionLength) {
            val permission = permissionArray[i]
            if (ActivityCompat.checkSelfPermission(context!!, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false
                break
            }
            i++
        }
        return allPermissionsGranted
    }


    fun openPermissionDialog(permissionType: PermissionType, listner: PermissionListener) {
        this.listener = listner

        val permissionArray = getPermissionArray(permissionType)
        var allPermissionsGranted = true
        var i = 0
        val mPermissionLength = permissionArray!!.size
        while (i < mPermissionLength) {
            val permission = permissionArray[i]
            if (ActivityCompat.checkSelfPermission(context!!, permission) != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = false
                break
            }
            i++
        }
        if (!allPermissionsGranted) {
            if (activity != null) {
                ActivityCompat.requestPermissions(activity!!, permissionArray, requestCode)
            } else fragment?.requestPermissions(permissionArray, requestCode)
        } else {
            listener!!.onPermissionGranted()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == this.requestCode) {
            var allPermissionGranted = true
            if (grantResults.size == permissions.size) {
                for (i in permissions.indices) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        allPermissionGranted = false
                        break
                    }
                }
            } else {
                allPermissionGranted = false
            }
            if (allPermissionGranted) {
                listener!!.onPermissionGranted()
            } else {
                listener!!.onPermissionDenied()
            }
        }
    }


}