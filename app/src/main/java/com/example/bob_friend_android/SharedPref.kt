package com.example.bob_friend_android

import android.content.Context
import android.content.SharedPreferences

import android.util.Log
import com.example.bob_friend_android.model.Token

object SharedPref {
    val LOGIN_SESSION = "login.session"

    var sharedPref: SharedPreferences? = null
    private var token: Token? = null

    fun openSharedPrep(context: Context): SharedPreferences {
        return context.getSharedPreferences(LOGIN_SESSION, Context.MODE_PRIVATE)
    }

    fun getToken() : Token? {
        if (token == null) {
            token = Token(sharedPref!!.getString("token", "")!!, sharedPref!!.getString("refresh", "")!!)
        }
        return token
    }

    fun saveToken(token: Token) {
        this.token = token
        val editor = App.prefs.edit()
        editor.putString("token", token.accessToken)
        editor.putString("refresh", token.refreshToken)
        editor.apply()
    }
}