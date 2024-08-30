package com.example.myapplication

import android.app.Application
import com.roam.connector.RoamMQTTConnector
import com.roam.sdk.Roam

class MainApplication:Application() {

    override fun onCreate() {
        super.onCreate()

        //roam core
        //Roam.initialize(this, "db4de26a9d8edc115a779d722dcc033d42018924024bea34ace18fc541236ff2")//expired
        Roam.initialize(this, "66126a7c68aebe84c5f2b1a6e1a6f45fc5f5928729554a02437e703d2086e358")

        //roam mqtt connector
        RoamMQTTConnector.initialize(this)
    }
}