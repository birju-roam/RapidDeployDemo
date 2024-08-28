package com.example.myapplication.services

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder

class ImplicitService:Service() {
    private var mLocationReceiver: LocationReceiver? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        register()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        unRegister()
    }

    private fun register() {
        mLocationReceiver = LocationReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction("com.roam.android.RECEIVED")
        intentFilter.addAction("com.roam.android.NETWORK")
        intentFilter.addAction("com.roam.android.CUSTOM.LOG")
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            registerReceiver(mLocationReceiver, intentFilter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(mLocationReceiver, intentFilter)
        }
    }

    private fun unRegister() {
        if (mLocationReceiver != null) {
            unregisterReceiver(mLocationReceiver)
        }
    }
}