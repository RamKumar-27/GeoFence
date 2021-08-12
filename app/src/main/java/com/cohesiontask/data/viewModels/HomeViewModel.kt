package com.cohesiontask.data.viewModels

import android.location.Location
import com.google.android.gms.location.Geofence
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.password.data.viewmodels.BaseViewModel


class HomeViewModel : BaseViewModel<Any?>() {
    private var callbacks: Callbacks? = null

    interface Callbacks {
        fun geofenceRequest(arrayList: ArrayList<Geofence>)
    }

    fun initCallbacks(callbacks: Callbacks) {
        this.callbacks = callbacks
    }


    /*animate the Map camera to user current location*/
    fun animateCameraToLatLng(location: Location, map: GoogleMap?) {
        val coordinate =
            LatLng(location.latitude, location.longitude)
        map?.let {
            it.moveCamera(CameraUpdateFactory.newLatLng(coordinate))
            it.animateCamera(CameraUpdateFactory.zoomTo(16f))
        }

    }

    /**Create GeoFence For the user selected location
     * @param latlng user selected latitude and longitude
     * */
    fun createGeoFence(latlng: LatLng,radius:Float) {
        val geofenceList = arrayListOf<Geofence>()
        geofenceList.add(
            Geofence.Builder()
                // Set the request ID of the geofence. This is a string to identify this
                // geofence.
                .setRequestId("1")
                // Set the circular region of this geofence.
                .setCircularRegion(
                    latlng.latitude,
                    latlng.longitude,
                    radius
                )
                // expiration duration of the geofence. It never Expire
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT)
                // Create the geofence.
                .build()
        )
        callbacks?.geofenceRequest(geofenceList)
    }
}