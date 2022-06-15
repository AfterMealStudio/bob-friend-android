package com.example.bob_friend_android.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bob_friend_android.App
import com.example.bob_friend_android.data.entity.Event
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

    private val _msg = MutableLiveData<Event<String>>()
    val errorMsg : LiveData<Event<String>> = _msg

    private val _token = MutableLiveData<Token>()
    val token : LiveData<Token> = _token


    private fun postValueEvent(value : Int, type: String) {
        val msgArrayList = arrayOf("Api 오류 : $type 실패했습니다.",
            "서버 오류 : $type 실패했습니다.",
            "알 수 없는 오류 : $type 실패했습니다."
        )

        when(value) {
            0 -> _msg.postValue(Event(msgArrayList[0]))
            1 -> _msg.postValue(Event(msgArrayList[1]))
            2 -> _msg.postValue(Event(msgArrayList[2]))
        }
    }


    fun login(email: String, password: String) {
        if(validation(email, password)) {
            val user = HashMap<String, String>()
            user["email"] = email
            user["password"] = password

            showProgress()
            viewModelScope.launch {
                val response = repository.signIn(user)
                val type = "로그인에"
                when(response) {
                    is NetworkResponse.Success -> {
                        _token.postValue(response.body)
                    }
                    is NetworkResponse.ApiError -> {
                        postValueEvent(0, type)
                    }
                    is NetworkResponse.NetworkError -> {
                        postValueEvent(1, type)
                    }
                    is NetworkResponse.UnknownError -> {
                        postValueEvent(2, type)
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
            val type = "로그인 연장에"
            when(response) {
                is NetworkResponse.Success -> {
                    _token.postValue(response.body)
                }
                is NetworkResponse.ApiError -> {
                    postValueEvent(0, type)
                }
                is NetworkResponse.NetworkError -> {
                    postValueEvent(1, type)
                }
                is NetworkResponse.UnknownError -> {
                    postValueEvent(2, type)
                }
            }
            hideProgress()
        }
    }


    fun validateUser() {
        showProgress()
        viewModelScope.launch {
            val response = repository.validateToken()
            val type = "토큰인증에"
            when(response) {
                is NetworkResponse.Success -> {
                    if (response.body.isValid){
                        _msg.postValue(Event("자동 로그인"))
                    }
                    else {
                        refreshToken(App.prefs.getString("token","token")!!, App.prefs.getString("refresh", "")!!)
                    }
                }
                is NetworkResponse.ApiError -> {
                    postValueEvent(0, type)
                }
                is NetworkResponse.NetworkError -> {
                    postValueEvent(1, type)
                }
                is NetworkResponse.UnknownError -> {
                    postValueEvent(2, type)
                }
            }

            hideProgress()
        }
    }


    private fun validation(username : String, password: String): Boolean {
        if (username.isEmpty() || password.isEmpty()) {
            _msg.postValue(Event("아이디와 비밀번호를 입력해주세요!"))
            return false
        }
        return true
    }
}