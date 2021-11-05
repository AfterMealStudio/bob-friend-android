package com.example.bob_friend_android.viewmodel

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import com.example.bob_friend_android.App
import com.example.bob_friend_android.network.KakaoAPI
import com.example.bob_friend_android.adapter.SearchAdapter
import com.example.bob_friend_android.model.SearchKeyword
import com.example.bob_friend_android.model.SearchLocation
import com.example.bob_friend_android.model.User
import com.example.bob_friend_android.network.RetrofitBuilder
import com.example.bob_friend_android.view.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val TAG = "MainViewModel"

    fun setUserInfo(headerUserName:TextView, headerEmail:TextView) {
        var nickname: String
        var email: String

        RetrofitBuilder.api.getUserId().enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                nickname = response.body()?.nickname.toString()
                email = response.body()?.email.toString()
                Log.d(TAG,"responese: $response, username: $nickname, email: $email")
                headerUserName.text = nickname
                headerEmail.text = email
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e(TAG, t.message.toString())
            }
        })
    }
}