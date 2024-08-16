package com.example.myapplication

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.myapplication.persistance.Preferences
import com.example.myapplication.services.LocationReceiver
import com.roam.connector.RoamMQTTConnector
import com.roam.connector.builders.RoamMqttConnectOptions
import com.roam.connector.builders.RoamMqttConnector
import com.roam.connector.enums.ConnectionType
import com.roam.sdk.Roam
import com.roam.sdk.builder.RoamPublish
import com.roam.sdk.builder.RoamTrackingMode
import com.roam.sdk.callback.PublishCallback
import com.roam.sdk.callback.RoamCallback
import com.roam.sdk.callback.SubscribeCallback
import com.roam.sdk.callback.TrackingCallback
import com.roam.sdk.models.RoamError
import com.roam.sdk.models.RoamUser
import org.json.JSONObject

class MainActivity : AppCompatActivity() {
    lateinit var userId: String
    lateinit var tvLocations: TextView
    lateinit var btLogout: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Register local receiver
        val filter = IntentFilter(LocationReceiver.ACTION_CUSTOM_BROADCAST)
        LocalBroadcastManager.getInstance(this).registerReceiver(localReceiver, filter)



        checkPermissions()
        registerConnector()

        tvLocations = findViewById(R.id.tvLocations)
        btLogout = findViewById(R.id.btLogout)
        btLogout.setOnClickListener(View.OnClickListener {
            stopTracking()
        })


    }

    private fun checkPermissions() {
        if (!Roam.checkLocationServices()) {
            Roam.requestLocationServices(this);
        } else if (!Roam.checkLocationPermission()) {
            Roam.requestLocationPermission(this);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !Roam.checkBackgroundLocationPermission()) {
            Roam.requestBackgroundLocationPermission(this);
        } else if (!Roam.checkActivityPermission()) {
            Roam.requestActivityPermission(this);
        } else {
            toggleEvents()

        }
    }

    /*
    * 1. toggleEvents()
    * 2. toggleListener()
    * 3. subscribe()
    * 4. publishLocationEvents()
    * 5. enableAccuracyEngine()
    * 6. updateLocationWhenStationary()
    * 7. startTracking()
    * */


    fun toggleEvents() {
        Roam.toggleEvents(false, false, true, false, object : RoamCallback {
            override fun onSuccess(p0: RoamUser?) {
                Log.e("TAG", "toggleEvents: success")
                toggleListener()
            }

            override fun onFailure(p0: RoamError?) {
                Log.e("TAG", "toggleEvents: failure")
                toggleEvents()

            }
        })
    }

    fun toggleListener() {
        Roam.toggleListener(true, true, object : RoamCallback {
            override fun onSuccess(p0: RoamUser?) {
                Log.e("TAG", "toggleListener: success")
                p0?.userId?.let {
                    subscribe(it)
                    //publishLocationEvents(it)
                }

            }

            override fun onFailure(p0: RoamError?) {
                Log.e("TAG", "toggleListener: failure")
            }
        })
    }

    fun subscribe(userId: String) {
        Roam.subscribe(Roam.Subscribe.LOCATION, userId, object : SubscribeCallback {
            override fun onSuccess(p0: String?, p1: String?) {
                Log.e("TAG", "subscribe: success")
                publishLocationEvents(userId)
            }

            override fun onError(p0: RoamError?) {
                Log.e("TAG", "subscribe: failure")
            }
        })
    }

    fun unsubscribe(userId: String) {
        Roam.unSubscribe(Roam.Subscribe.LOCATION, userId, object : SubscribeCallback {
            override fun onSuccess(p0: String?, p1: String?) {
                Log.e("TAG", "unsubscribe: success")
            }

            override fun onError(p0: RoamError?) {
                Log.e("TAG", "unsubscribe: failure")
            }
        })
    }

    fun publishLocationEvents(userId: String) {
        val jsonObject = JSONObject(
            mapOf(
                "platform" to 2,
                "handle" to "test", // TODO: Add a handle
            )
        )

        val publishBuilder = RoamPublish.Builder()
            .locationEvents()
            .metadata(jsonObject)
            .batteryRemaining()

        // if (BuildConfig.MOCK_LOCATIONS_ALLOWED) publishBuilder.allowMocked()

        val publish = publishBuilder.build()

        Roam.publishAndSave(publish, object : PublishCallback {
            override fun onSuccess(p0: String?) {
                Log.e("TAG", "publishAndSave: success")
                startTracking()
            }

            override fun onError(p0: RoamError?) {
                Log.e("TAG", "publishAndSave: failure")
            }
        })
    }


    private fun startTracking() {

        if (Preferences.isForegroundMethod(this@MainActivity)) {
            Roam.setForegroundNotification(
                true,
                "Roam Example App",
                "Click here to redirect the app",
                R.drawable.ic_launcher_background,
                "com.example.myapplication.MainActivity",
                "com.example.myapplication.services.ImplicitService"
            )
        }

        Roam.enableAccuracyEngine()
        Roam.updateLocationWhenStationary(300)
        Roam.startTracking(RoamTrackingMode.ACTIVE, object : TrackingCallback {
            override fun onSuccess(p0: String?) {
                Log.e("TAG", "Tracking: started")
            }

            override fun onError(p0: RoamError?) {
                Log.e("TAG", "Tracking: failure")
                startTracking()

            }
        })
    }

    private fun showMsg(msg: String) {

        Toast.makeText(
            this, msg,
            Toast.LENGTH_SHORT
        ).show()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        when (requestCode) {
            Roam.REQUEST_CODE_LOCATION_PERMISSION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermissions()
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                showMsg("Location permission required")
            }

            Roam.REQUEST_CODE_BACKGROUND_LOCATION_PERMISSION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermissions()
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                showMsg("Background Location permission required")
            }

            Roam.REQUEST_CODE_ACTIVITY_RECOGNITION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermissions()
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                showMsg("Activity reorganization permission required")
            }

            Roam.REQUEST_CODE_STORAGE_PERMISSION -> if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // exportLog();
            } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                showMsg("Write storage permission required")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Roam.REQUEST_CODE_LOCATION_ENABLED) {
            checkPermissions()
        }
    }

    fun stopTracking() {

        Roam.stopTracking(object : TrackingCallback {
            override fun onSuccess(p0: String?) {
                Log.e("TAG", "Tracking: Stop")

                Preferences.removeItem(this@MainActivity, "LOGIN")
                loginIntent()


            }

            override fun onError(p0: RoamError?) {
                TODO("Not yet implemented")
            }

        })
    }

    private fun loginIntent() {
        val intent: Intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }


    //listen broadcast locations
    private val localReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                if (it.action == LocationReceiver.ACTION_CUSTOM_BROADCAST) {
                    val data = it.getStringExtra(LocationReceiver.EXTRA_DATA)
                    //val dataArray = it.getParcelableArrayListExtra<RoamLocation>(LocationReceiver.EXTRA_DATA)

                    // Handle the data here
                    Toast.makeText(this@MainActivity, "Received data: $data", Toast.LENGTH_SHORT)
                        .show()

                    tvLocations.text = data

                }
            }
        }
    }

    override fun onDestroy() {
        // Unregister local receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localReceiver)
        super.onDestroy()
    }

    //custom mqtt connector
    private fun registerConnector() {

        //optional: based on requirement
//            val roamMqttConnectOptions: RoamMqttConnectOptions = Builder()
//                .setAutomaticReconnect(autoReconnect)
//                .setConnectionTimeout(connectionTimeout)
//                .setCleanSession(cleanSession)
//                .setKeepAliveInterval(keepAliveInterval)
//                .build()

        val roamMqttConnector: RoamMqttConnector = RoamMqttConnector.Builder(
            "broker.mqtt.cool", 1883, ConnectionType.TCP,
            "test"
        )

            //optional: based on requirement
//                .setClientId(etClientId.getText().toString())
//                .setPath(etPath.getText().toString())
//                .setUserName(etUserName.getText().toString())
//                .setPassword(etPassword.getText().toString())
//                .setQos(qos)
//                .setConnectionOptions(roamMqttConnectOptions)
//                .setWill(willTopic, willPayload, willQos, retained)

            .build()

        RoamMQTTConnector.registerConnector(roamMqttConnector)
    }

    private fun deRegisterConnector() {
        RoamMQTTConnector.deregisterConnector()
    }

}
