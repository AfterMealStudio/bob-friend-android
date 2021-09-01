package com.example.bob_friend_android

import android.content.Context
import android.content.SharedPreferences

import android.util.Log

object SharedPref {
    val LOGIN_SESSION = "login.session"

    var sharedPref: SharedPreferences? = null

    fun openSharedPrep(context: Context): SharedPreferences {
        return context.getSharedPreferences(LOGIN_SESSION, Context.MODE_PRIVATE)
    }

    fun writeLoginSession(data: String) {
        if(this.sharedPref == null) {
            Log.e("DSMAD", "Plz start openSahredPrep() !")
        } else {
            sharedPref?.edit()?.putString("session", data)?.apply()
        }
    }

    fun readLoginSession() : String? {
        return if(this.sharedPref == null) {
            Log.e("DSMAD", "Plz start openSahredPrep() !")
            null
        } else sharedPref?.getString("session", null)
    }

    fun getString(key: String, defValue: String): String {
        return sharedPref?.getString(key, defValue).toString()
    }

    fun setString(key: String, str: String) {
        sharedPref?.edit()?.putString(key, str)?.apply()
    }
}