package com.example.bob_friend_android.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bob_friend_android.App
import com.example.bob_friend_android.ui.view.base.BaseViewModel
import com.example.bob_friend_android.data.entity.Token
import com.example.bob_friend_android.data.network.NetworkResponse
import com.example.bob_friend_android.data.repository.login.LoginRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository
): BaseViewModel() {

    private val _msg = MutableLiveData<String>()
    val errorMsg : LiveData<String>
        get() = _msg

    private val _token = MutableLiveData<Token>()
    val token : LiveData<Token>
        get() = _token


    fun login(email: String, password: String) {
        if(validation(email, password)) {
            val user = HashMap<String, String>()
            user["email"] = email
            user["password"] = password

            showProgress()
            viewModelScope.launch {
                val response = repository.signIn(user)
                when(response) {
                    is NetworkResponse.Success -> {
                        _token.postValue(response.body)
                    }
                    is NetworkResponse.ApiError -> {
                        _msg.postValue("Api 오류 : 로그인에 실패했습니다.")
                    }
                    is NetworkResponse.NetworkError -> {
                        _msg.postValue("서버 오류 : 로그인에 실패했습니다.")
                    }
                    is NetworkResponse.UnknownError -> {
                        _msg.postValue("알 수 없는 오류 : 로그인에 실패했습니다.")
                    }
                }
                hideProgress()
            }
        }
    }


    fun refreshToken(access: String, refresh: String) {
        val token = Token(access, refresh)
        showProgress()
        viewModelScope.launch {
            val response = repository.refreshToken(token)
            when(response) {
                is NetworkResponse.Success -> {
                    _token.postValue(response.body)
                }
                is NetworkResponse.ApiError -> {
                    _msg.postValue("Api 오류 : 로그인 연장에 실패했습니다.")
                }
                is NetworkResponse.NetworkError -> {
                    _msg.postValue("서버 오류 : 로그인 연장에 실패했습니다.")
                }
                is NetworkResponse.UnknownError -> {
                    _msg.postValue("알 수 없는 오류 : 로그인 연장에 실패했습니다.")
                }
            }
            hideProgress()
        }
    }


    fun validateUser() {
        showProgress()
        viewModelScope.launch {
            val response = repository.validateToken()
            when(response) {
                is NetworkResponse.Success -> {
                    if (response.body.isValid){
                        _msg.postValue("자동 로그인")
                    }
                    else {
                        refreshToken(App.prefs.getString("token","token")!!, App.prefs.getString("refresh", "")!!)
                    }
                }
                is NetworkResponse.ApiError -> {
                    _msg.postValue("Api 오류 : 토큰 인증에 실패했습니다.")
                }
                is NetworkResponse.NetworkError -> {
                    _msg.postValue("서버 오류 : 토큰 인증에 실패했습니다.")
                }
                is NetworkResponse.UnknownError -> {
                    _msg.postValue("알 수 없는 오류 : 토큰 인증에 실패했습니다.")
                }
            }

            hideProgress()
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