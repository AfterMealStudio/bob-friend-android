package com.example.bob_friend_android.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.example.bob_friend_android.App
import com.example.bob_friend_android.model.User
import com.example.bob_friend_android.network.RetrofitBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val TAG = "MainViewModel"

    fun setUserInfo() {
        var nickname: String
        var email: String
        var birth: String
        var sex: String
        var id: Int

        RetrofitBuilder.api.getUserId().enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                nickname = response.body()?.nickname.toString()
                email = response.body()?.email.toString()
                birth = response.body()?.birth.toString()
                sex = response.body()?.sex.toString()
                id = response.body()?.id!!

                Log.d(TAG,"responese: $response, username: $nickname, email: $email, id : $id")

                val editor = App.prefs.edit()
                editor.putInt("id", id)
                editor.putString("email", email)
                editor.putString("nickname", nickname)
                editor.putString("birth", birth)
                editor.putString("sex", sex)
                editor.apply()
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e(TAG, t.message.toString())
            }
        })
    }
}