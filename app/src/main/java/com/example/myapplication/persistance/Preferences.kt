package com.example.myapplication.persistance

import android.content.Context
import android.content.SharedPreferences
import java.lang.reflect.Array.setBoolean
import java.util.Locale


class Preferences {

    companion object {
        private val shared_prefer = "roam"

        private fun getInstance(context: Context): SharedPreferences {
            return context.getSharedPreferences(shared_prefer, Context.MODE_PRIVATE)
        }

        private fun setBoolean(context: Context, key: String, value: Boolean) {
            val editor = getInstance(context).edit()
            editor.putBoolean(key, value)
            editor.apply()
            editor.commit()
        }

        private fun getBoolean(context: Context, key: String): Boolean {
            return getInstance(context).getBoolean(key, false)
        }

        private fun setInt(context: Context, key: String, value: Int) {
            val editor = getInstance(context).edit()
            editor.putInt(key.uppercase(Locale.getDefault()), value)
            editor.apply()
            editor.commit()
        }

        private fun getInt(context: Context, key: String): Int {
            return getInstance(context).getInt(key.uppercase(Locale.getDefault()), 0)
        }

        private fun setString(context: Context, key: String, value: String) {
            val editor = getInstance(context).edit()
            editor.putString(key.uppercase(Locale.getDefault()), value)
            editor.apply()
            editor.commit()
        }

        private fun getString(context: Context, key: String): String? {
            return getInstance(context).getString(key.uppercase(Locale.getDefault()), "")
        }


        fun removeItem(context: Context, key: String?) {
            val editor = getInstance(context).edit()
            editor.remove(key)
            editor.apply()
            editor.commit()
        }

        fun setLogin(context: Context?, value: Boolean) {
            setBoolean(context!!, "LOGIN", value)
        }

        fun isLoggedIn(context: Context?): Boolean {
            return getBoolean(context!!, "LOGIN")
        }

        fun setUserId(context: Context?, value: String) {
            setString(context!!, "USER", value)
        }

        fun getUserId(context: Context?): String? {
            return getString(context!!, "USER")
        }

        fun setForegroundMethod(context: Context?, value: Boolean) {
            setBoolean(context!!, "FOREGROUND", value)
        }

        fun isForegroundMethod(context: Context?): Boolean {
            return getBoolean(context!!, "FOREGROUND")
        }
    }
}