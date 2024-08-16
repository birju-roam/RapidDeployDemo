package com.example.myapplication

import android.app.Application
import com.roam.connector.RoamMQTTConnector
import com.roam.sdk.Roam

class MainApplication:Application() {

    override fun onCreate() {
        super.onCreate()

        //roam core
        Roam.initialize(this, "db4de26a9d8edc115a779d722dcc033d42018924024bea34ace18fc541236ff2")

        //roam mqtt connector
        RoamMQTTConnector.initialize(this)
    }
}