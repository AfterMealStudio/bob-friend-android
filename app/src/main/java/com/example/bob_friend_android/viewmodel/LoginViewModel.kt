package com.example.bob_friend_android.viewmodel

import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
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
    val PREFERENCE = "bob_friend_android"

    fun login(username: String, password: String, checked: Boolean,context: Context) {
        if(validation(username, password, context)) {
            val user = HashMap<String, String>()
            user["username"] = username
            user["password"] = password

            Log.d(TAG, user.toString())

            RetrofitBuilder.api.getLoginResponse(user).enqueue(object : Callback<Token> {
                override fun onResponse(call: Call<Token>, response: Response<Token>) {
                    when (response.code()) {
                        200 -> {
                            Log.d(TAG, "response : ${response.body()?.token}")
                            val editor = App.prefs.edit()
                            editor.putString("username", username)
                            editor.putString("token", response.body()?.token.toString())
                            editor.putBoolean("checked", checked)
                            editor.apply()
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        }
                        405 -> Toast.makeText(context, "로그인 실패 : 아이디나 비번이 올바르지 않습니다", Toast.LENGTH_LONG).show()
                        500 -> Toast.makeText(context, "로그인 실패 : 서버 오류", Toast.LENGTH_LONG).show()
                        else -> Toast.makeText(context, "로그인 실패 : 아이디나 비번이 올바르지 않습니다", Toast.LENGTH_LONG).show()
                    }
                }
                override fun onFailure(call: Call<Token>, t: Throwable) {
                    Toast.makeText(context, "서버에 연결이 되지 않았습니다. 다시 시도해주세요!", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, t.message.toString())
                }

            })
        }
    }

    fun validateUser(token: String, context: Context) {
        RetrofitBuilder.api.getToken(token).enqueue(object : Callback<Boolean> {
            override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                Log.d(TAG, "validateUser: ${response.body()}")
                if (response.body() != null){
                    if(response.body()!!) {
                        val intent = Intent(context, MainActivity::class.java)
                        context.startActivity(intent)
                    }
                }
            }

            override fun onFailure(call: Call<Boolean>, t: Throwable) {
                Log.d(TAG, "ttt: $t")
            }
        })
    }

    private fun validation(username : String, password: String, context: Context): Boolean {
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(context, "아이디와 비밀번호를 입력해주세요!", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
}