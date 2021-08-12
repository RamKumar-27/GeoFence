package com.cohesiontask.reciver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.cohesiontask.R
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceReceiver : BroadcastReceiver() {

    //his this method when the user enter or exit from the location
    override fun onReceive(context: Context?, intent: Intent?) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if (geofencingEvent.hasError()) {
            val errorMessage = GeofenceStatusCodes
                .getStatusCodeString(geofencingEvent.errorCode)
            Log.e("GeoFenceError", errorMessage)
            return
        }

        // Get the transition type.
        val geofenceTransition = geofencingEvent.geofenceTransition

        //currently we are showing the notification to the user
        //If we need API means we can call the API from here
        when (geofenceTransition) {
            Geofence.GEOFENCE_TRANSITION_ENTER ->
                showNotification(context, context!!.getString(R.string.entered_the_location))
            Geofence.GEOFENCE_TRANSITION_EXIT ->
                showNotification(context, context!!.getString(R.string.exit_the_location))
            else -> showNotification(context, "Something Wrong")

        }
    }

    /*
    * show notification to the user that they entered into the location
    * */
    private fun showNotification(context: Context?, message: String) {
        val notificationManager =
            context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        var builder: NotificationCompat.Builder? = null
        builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val notificationChannel = NotificationChannel("ID", "Name", importance)
            notificationManager.createNotificationChannel(notificationChannel)
            NotificationCompat.Builder(
                context,
                notificationChannel.id
            )
        } else {
            NotificationCompat.Builder(context)
        }

        builder = builder
            .setContentTitle("GeoFence Callback")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentText(message)
            .setDefaults(Notification.DEFAULT_ALL)
            .setAutoCancel(true)
        notificationManager.notify(1, builder.build())
    }
}