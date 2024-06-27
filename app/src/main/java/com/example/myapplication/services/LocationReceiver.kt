package com.example.myapplication.services

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.gson.Gson
import com.roam.sdk.models.NetworkListener
import com.roam.sdk.models.RoamError
import com.roam.sdk.models.RoamLocation
import com.roam.sdk.models.RoamLocationReceived
import com.roam.sdk.models.RoamTripStatus
import com.roam.sdk.models.events.RoamEvent
import com.roam.sdk.service.RoamReceiver

class LocationReceiver:RoamReceiver() {
    companion object {
        const val ACTION_CUSTOM_BROADCAST = "com.example.ACTION_CUSTOM_BROADCAST"
        const val EXTRA_DATA = "com.example.EXTRA_DATA"

        lateinit var location:String
    }
    override fun onLocationUpdated(context: Context?, locationList: List<RoamLocation>) {
        super.onLocationUpdated(context, locationList)
        if (locationList[0].location != null) {
            Log.e(
                "TAG",
                "onLocationUpdated: " + locationList.size + "   LAT: " + locationList[0].location.latitude + " LNG: " + locationList[0].location.longitude + " Accuracy: " + locationList[0].location.accuracy
            )
            Toast.makeText(
                context,
                "Location1: " + "Lat: " + locationList[0].location.latitude + " Lng: " + locationList[0].location.longitude,
                Toast.LENGTH_SHORT
            ).show()

            location = "lat:"+locationList[0].location.latitude+"\n lon:"+locationList[0].location.longitude+"\n speed:"+locationList[0].location.speed+"\n accuracy:"+locationList[0].location.accuracy+"\n altitude:"+locationList[0].location.altitude+"\n recordedAt:"+locationList[0].recordedAt
            //pass data
            // Send local broadcast
//            val localIntent = Intent(ACTION_CUSTOM_BROADCAST).apply {
//                putParcelableArrayListExtra(EXTRA_DATA, ArrayList(locationList))
//            }
            val localIntent = Intent(ACTION_CUSTOM_BROADCAST).apply {
                putExtra(EXTRA_DATA, location)
            }
            LocalBroadcastManager.getInstance(context!!).sendBroadcast(localIntent)
        }
    }

    override fun onLocationReceived(context: Context?, roamLocationReceived: RoamLocationReceived) {
        Log.e("TAG", "onReceive: " + Gson().toJson(roamLocationReceived))
        Toast.makeText(
            context,
            "Location2: " + "Lat: " + roamLocationReceived.latitude + " Lng: " + roamLocationReceived.longitude,
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onEventReceived(context: Context?, roamEvent: RoamEvent) {
        try {
            Log.e("Events ", roamEvent.user_id + " " + roamEvent.event_type)
            Toast.makeText(
                context,
                "Location2: " + "Lat: " + roamEvent.user_id + " " + roamEvent.event_type,
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            // Log.e("LocationReceivedError ", e.getMessage());
        }
    }


    override fun onReceiveTrip(context: Context?, listener: List<RoamTripStatus>?) {
        Log.e("TAG", "onReceiveTripStatus: " + Gson().toJson(listener))
        if (listener != null) {

        }
    }

    override fun onConnectivityChange(context: Context?, networkListener: NetworkListener?) {
        Log.e("TAG", "onNetworkStateChanged: " + Gson().toJson(networkListener))
        Toast.makeText(
            context,
            "networkState " + Gson().toJson(networkListener),
            Toast.LENGTH_SHORT
        ).show()
    }

    override fun onError(context: Context?, roamError: RoamError) {
        Log.e("onError: ", roamError.code + " " + roamError.message)
        Toast.makeText(context, "onError: " + roamError.message, Toast.LENGTH_SHORT).show()
    }

    override fun mqttLogger(message: String?, timeStamp: Long) {
        Log.e("mqttLogger: ", (message)!!)
    }
}