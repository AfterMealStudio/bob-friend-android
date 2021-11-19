package com.example.bob_friend_android.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bob_friend_android.App
import com.example.bob_friend_android.model.Board
import com.example.bob_friend_android.model.User
import com.example.bob_friend_android.network.RetrofitBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val TAG = "MainViewModel"

    private val _userInfo = MutableLiveData<User>()
    val userInfo : LiveData<User>
        get() = _userInfo

    private val _msg = MutableLiveData<String>()
    val errorMsg : LiveData<String>
        get() = _msg

    fun setUserInfo() {
        RetrofitBuilder.apiBob.getUserId().enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val code = response.code()
                if (code == 200) {
                    Log.d(TAG, "setUserInfo: $response")
                    _userInfo.postValue(response.body())
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다.")
                Log.e(TAG, t.message.toString())
            }
        })
    }
}