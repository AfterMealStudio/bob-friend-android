package com.example.bob_friend_android.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bob_friend_android.App
import com.example.bob_friend_android.ui.view.base.BaseViewModel
import com.example.bob_friend_android.data.entity.Token
import com.example.bob_friend_android.data.entity.UserCheck
import com.example.bob_friend_android.di.ApiModule
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel: BaseViewModel() {

    val TAG = "LoginViewModel"

    private val _msg = MutableLiveData<String>()
    val errorMsg : LiveData<String>
        get() = _msg

    private val _token = MutableLiveData<Token>()
    val token : LiveData<Token>
        get() = _token

    private val _refreshToken = MutableLiveData<Token>()
    val refreshToken : LiveData<Token>
        get() = _refreshToken

    private val _progressVisible = MutableLiveData<Boolean>()
    val progressVisible : LiveData<Boolean>
        get() = _progressVisible


    fun login(email: String, password: String) {
        if(validation(email, password)) {
            val user = HashMap<String, String>()
            user["email"] = email
            user["password"] = password

            Log.d(TAG, user.toString())

            _progressVisible.postValue(true)
            ApiModule.apiBob.getLoginResponse(user).enqueue(object : Callback<Token> {
                override fun onResponse(call: Call<Token>, response: Response<Token>) {
                    when (response.code()) {
                        200 -> _token.postValue(response.body())
                        405 -> _msg.postValue("로그인 실패 : 아이디나 비번이 올바르지 않습니다!")
                        500 -> _msg.postValue("로그인 실패 : 서버 오류입니다.")
                        else -> _msg.postValue("로그인에 실패했습니다. ${response.errorBody()?.toString()}")
                    }

                    _progressVisible.postValue(false)
                }
                override fun onFailure(call: Call<Token>, t: Throwable) {
                    _msg.postValue("서버에 연결이 되지 않았습니다.")
                    Log.e(TAG, t.message.toString())
                    _progressVisible.postValue(false)
                }
            })
        }
    }


    fun refreshToken(access: String, refresh: String) {
        val token = Token(access, refresh)
        _progressVisible.postValue(true)
        viewModelScope.launch {
            ApiModule.apiBob.refreshToken(token).enqueue(object : Callback<Token> {
                override fun onResponse(call: Call<Token>, response: Response<Token>) {
                    when (response.code()) {
                        200 -> _refreshToken.postValue(response.body())
                        500 -> _msg.postValue("로그인 실패 : 서버 오류입니다.")
                        else -> _msg.postValue("자동 로그인에 실패했습니다. ${response.errorBody()?.string()}")
                    }
                    _progressVisible.postValue(false)
                }

                override fun onFailure(call: Call<Token>, t: Throwable) {
                    _msg.postValue("서버에 연결이 되지 않았습니다.")
                    Log.d(TAG, "ttt: $t")
                }
            })
        }
    }


    fun validateUser() {
        _progressVisible.postValue(true)
        viewModelScope.launch {
            ApiModule.apiBob.getToken().enqueue(object : Callback<UserCheck> {
                override fun onResponse(call: Call<UserCheck>, response: Response<UserCheck>) {
                    Log.d(TAG, "validateUser: ${response.body()}")
                    if (response.body() != null) {
                        if (response.body()!!.isValid) {
                            _msg.postValue("자동 로그인")
                        }
                        else {
                            refreshToken(App.prefs.getString("token","token")!!, App.prefs.getString("refresh", "")!!)
                        }
                    }
                    _progressVisible.postValue(false)
                }

                override fun onFailure(call: Call<UserCheck>, t: Throwable) {
                    _msg.postValue("서버에 연결이 되지 않았습니다.")
                    Log.d(TAG, "ttt: $t")
                }
            })
        }
    }


    private fun validation(username : String, password: String): Boolean {
        if (username.isEmpty() || password.isEmpty()) {
            _msg.postValue("아이디와 비밀번호를 입력해주세요!")
            return false
        }
        return true
    }
}