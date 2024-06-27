package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.persistance.Preferences
import com.roam.sdk.Roam
import com.roam.sdk.callback.RoamCallback
import com.roam.sdk.models.RoamError
import com.roam.sdk.models.RoamUser


class LoginActivity : AppCompatActivity() {

    private lateinit var btLogin: Button
    private lateinit var checkFor: CheckBox
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (Preferences.isLoggedIn(this)) {
            homeIntent()
        }

        btLogin = findViewById(R.id.btLogin)
        checkFor = findViewById(R.id.checkFor)
        btLogin.setOnClickListener(View.OnClickListener {
            login()
        })
        checkFor.setOnCheckedChangeListener { buttonView, isChecked ->

            Toast.makeText(this,"Foreground service: "+isChecked.toString(),Toast.LENGTH_SHORT).show()
        }
    }

    private fun login() {

       if(checkFor.isChecked){
           Preferences.setForegroundMethod(this,true)
       }else{
           Preferences.setForegroundMethod(this,false)
       }

        if (TextUtils.isEmpty(Preferences.getUserId(this))) {
            Roam.createUser("Test-user", null, object : RoamCallback {
                override fun onSuccess(roamUser: RoamUser) {
                    Log.e("TAG", "onSuccess: " + roamUser.userId)
                    Preferences.setLogin(this@LoginActivity,true)
                    Preferences.setUserId(this@LoginActivity, roamUser.userId)
                    homeIntent()

                }

                override fun onFailure(error: RoamError) {
                    Log.e("TAG", "onFailure: " + error.message)
                }
            })
        } else {
            val userId = Preferences.getUserId(this)
            Roam.getUser(userId, object : RoamCallback {
                override fun onSuccess(p0: RoamUser?) {
                    Preferences.setLogin(this@LoginActivity,true)
                    Log.e("TAG", "onSuccess: " + p0?.userId)
                    homeIntent();
                }

                override fun onFailure(p0: RoamError?) {
                    Log.e("TAG", "onFailure: " + p0?.message)
                }

            })
        }


    }

    private fun homeIntent() {
        val intent: Intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }


}