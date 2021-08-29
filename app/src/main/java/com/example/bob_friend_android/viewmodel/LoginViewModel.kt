package com.example.bob_friend_android.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.media.session.MediaSession
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import com.example.bob_friend_android.App
import com.example.bob_friend_android.model.Token
import com.example.bob_friend_android.network.RetrofitBuilder
import com.example.bob_friend_android.view.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(application: Application): AndroidViewModel(application) {

    val TAG = "LoginViewModel"

    fun login(username: String, password: String, context: Context) {
        if(validation(username, password, context)) {
            val user = HashMap<String, String>()
            user["username"] = username
            user["password"] = password

            Log.d(TAG, user.toString())

            RetrofitBuilder.api.getLoginResponse(user).enqueue(object : Callback<Token> {
                override fun onResponse(call: Call<Token>, response: Response<Token>) {
                    if (response.isSuccessful && response.body() != null) {
                        val intent = Intent(context, MainActivity::class.java)
                        App.prefs.setString("token", response.body()!!.token)
                        App.prefs.setString("username", username)
                        App.prefs.setString("password", password)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        context.startActivity(intent)
                        Log.d(TAG, "onResponse not null")
                    } else {
                        Log.d(TAG, "response : " + response.body().toString())
                        val error = response.errorBody()!!.string()
                        if (error == "100") {
                            Toast.makeText(context, "가입되지 않은 사용자입니다.", Toast.LENGTH_SHORT).show()
                        } else if (error == "200") {
                            Toast.makeText(context, "잘못된 비밀번호 입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                override fun onFailure(call: Call<Token>, t: Throwable) {
                    Toast.makeText(context, "서버에 연결이 되지 않았습니다. 다시 시도해주세요!", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, t.message.toString())
                }

            })
        }
    }

    private fun validation(username : String, password: String, context: Context): Boolean {
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "아이디와 비밀번호를 입력해주세요!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}