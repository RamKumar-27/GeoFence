package com.cohesiontask.ui

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.cohesiontask.R
import com.cohesiontask.app.PermissionHelper
import com.cohesiontask.data.viewModels.HomeViewModel
import com.cohesiontask.databinding.ActivityHomeBinding
import com.cohesiontask.reciver.GeofenceReceiver
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import java.lang.RuntimeException


class HomeActivity : AppCompatActivity(), OnMapReadyCallback, HomeViewModel.Callbacks {

    private var currentLocation: Location? = null
    private var viewModel: HomeViewModel? = null
    private var homeBinding: ActivityHomeBinding? = null
    private var permissionHelper: PermissionHelper? = null
    private val REQUEST_CODE_LOCATION_ENABLE = 2481
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var googleMap: GoogleMap? = null
    lateinit var geofencingClient: GeofencingClient
    private var selectedLatLng: LatLng? = null
    private var radius = 100.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeBinding = ActivityHomeBinding.inflate(LayoutInflater.from(this))
        setContentView(homeBinding?.root)

        //Without viewmodelfactory
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel?.initCallbacks(this)
        permissionHelper = PermissionHelper(this)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geofencingClient = LocationServices.getGeofencingClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(
                R.id.map
            ) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
        listeners()
    }

    /*enable the common view listeners*/
    private fun listeners() {
        homeBinding?.let {
            it.seekBar.max = 1000
            it.seekBar.min = 100
            it.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    radius = progress.toDouble()
                    showConfirmation()
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {
                }

                override fun onStopTrackingTouch(seekBar: SeekBar?) {
                }

            })

            //Cancel button click callback
            it.cancelBtn.setOnClickListener { _ ->
                googleMap?.clear()
                it.geofenceGroup.visibility = View.GONE
            }

            //EnableButton Click callback
            it.enableButton.setOnClickListener { _ ->
                viewModel?.createGeoFence(selectedLatLng!!,radius.toFloat())
                it.geofenceGroup.visibility = View.GONE
            }
        }
    }


    override fun onMapReady(googlemap: GoogleMap?) {
        this.googleMap = googlemap
        this.googleMap?.setOnMapClickListener {
            if (currentLocation != null) {
                selectedLatLng = it
                showConfirmation()
                homeBinding?.geofenceGroup?.visibility = View.VISIBLE
            }
        }
        verifyLocationPermission()
    }

    /**
     * show the circle in the map to kow the Geofence circle
     * */
    private fun showConfirmation() {
        googleMap?.let {
            it.clear()
            it.addCircle(
                CircleOptions()
                    .center(selectedLatLng)
                    .radius(radius)
                    .strokeWidth(0f)
                    .fillColor(0x550000FF)
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionHelper!!.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /*
    * verify the location permission is enabled or not
    * if the location is not enable it will ask the permission
    * */
    private fun verifyLocationPermission() {
        if (permissionHelper!!.isPermissionGranted(PermissionHelper.PermissionType.LOCATION))
            openLocationEnableDialog()
        else permissionHelper!!.openPermissionDialog(PermissionHelper.PermissionType.LOCATION,
            object : PermissionHelper.PermissionListener {
                override fun onPermissionGranted() {
                    openLocationEnableDialog()
                }

                override fun onPermissionDenied() {
                    showToast("Permission Denied")

                }
            })
    }


    /*Enable the user GPS location*/
    private fun openLocationEnableDialog() {
        val locationRequest = LocationRequest.create()
            .setInterval(30)
            .setFastestInterval(30)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        LocationServices
            .getSettingsClient(this)
            .checkLocationSettings(builder.build())
            .addOnSuccessListener(
                this
            ) {
                getCurrentLocation()
            }
            .addOnFailureListener(
                this
            ) { ex: Exception? ->
                if (ex is ResolvableApiException) {
                    // Location settings are NOT satisfied,  but this can be fixed  by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),  and check the result in onActivityResult().
                        ex.startResolutionForResult(
                            this,
                            REQUEST_CODE_LOCATION_ENABLE
                        )
                    } catch (sendEx: IntentSender.SendIntentException) {
                        // Ignore the error.
                    }
                }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (REQUEST_CODE_LOCATION_ENABLE == requestCode) {
            if (RESULT_OK == resultCode) {
                getCurrentLocation()
            }
        }

    }

    /*
    * find the user current location latitude and longitude
    * */
    private fun getCurrentLocation() {
        googleMap?.isMyLocationEnabled = true
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        mFusedLocationClient?.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            null
        )?.addOnCompleteListener {
            val location: Location? = it.result
            location?.let {
                currentLocation = it
                viewModel?.animateCameraToLatLng(it, googleMap)
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
    }

    override fun geofenceRequest(arrayList: ArrayList<Geofence>) {
        val request = GeofencingRequest.Builder().apply {
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            addGeofences(arrayList)
        }.build() // build the GeoFenceRequest

        val intent = Intent(this, GeofenceReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) return
        geofencingClient.addGeofences(request, pendingIntent).run {
            addOnSuccessListener {
                showToast(getString(R.string.geofence_enabled))
            }
            addOnFailureListener {
                showToast(getString(R.string.geofence_error))
            }
        }
    }

}