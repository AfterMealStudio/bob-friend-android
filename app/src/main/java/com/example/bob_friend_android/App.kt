package com.example.bob_friend_android

import android.app.Application
import android.content.SharedPreferences

class App : Application() {

    companion object {
        lateinit var prefs : SharedPreferences
    }

    override fun onCreate() {
        prefs = SharedPref.openSharedPrep(applicationContext)
        super.onCreate()
    }
}