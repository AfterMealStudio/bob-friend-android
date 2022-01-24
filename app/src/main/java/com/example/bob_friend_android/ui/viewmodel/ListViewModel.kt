package com.example.bob_friend_android.ui.viewmodel

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bob_friend_android.data.entity.AppointmentList
import com.example.bob_friend_android.data.entity.ErrorResponse
import com.example.bob_friend_android.data.entity.Token
import com.example.bob_friend_android.data.network.NetworkResponse
import com.example.bob_friend_android.data.repository.list.ListRepository
import com.example.bob_friend_android.ui.view.base.BaseViewModel
import com.example.bob_friend_android.model.Location
import com.example.bob_friend_android.model.SearchKeyword
import com.example.bob_friend_android.model.*
import com.example.bob_friend_android.di.ApiModule
import com.google.gson.GsonBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val repository: ListRepository
): BaseViewModel() {

    private val API_KEY = "KakaoAK 81e4657cca25cf97b1cec85102769390"

    private val _appointmentList = MutableLiveData<AppointmentList>()
    val appointmentList : MutableLiveData<AppointmentList>
        get() = _appointmentList

    private val _msg = MutableLiveData<String>()
    val errorMsg : LiveData<String>
        get() = _msg

    private val _searchKeyword = MutableLiveData<SearchKeyword>()
    val searchKeyword : LiveData<SearchKeyword>
        get() = _searchKeyword

    private val _location = MutableLiveData<List<Location>>()
    val location : MutableLiveData<List<Location>>
        get() = _location

    private val _refreshToken = MutableLiveData<Token>()
    val refreshToken : LiveData<Token>
        get() = _refreshToken


    fun setAppointmentList(page: Int, type: String? = null, address: String? = null){
        var lastPage: Boolean
        var element: Int
        showProgress()
        viewModelScope.launch {
            val response = repository.setAppointmentList(page, type, address)
            when(response) {
                is NetworkResponse.Success -> {
                    element = response.body.element
                    lastPage = response.body.last
                    if(!lastPage||(element != 0 && lastPage)){
                        for (document in response.body.boardList) {
                            _appointmentList.postValue(response.body)
                        }
                    }
                }
                is NetworkResponse.ApiError -> {
                    _msg.postValue("Api 오류 : 약속 불러오는 것을 실패했습니다.")
                }
                is NetworkResponse.NetworkError -> {
                    _msg.postValue("서버 오류 : 약속 불러오는 것을 실패했습니다.")
                }
                is NetworkResponse.UnknownError -> {
                    _msg.postValue("알 수 없는 오류 : 약속 불러오는 것을 실패했습니다.")
                }
            }
            hideProgress()
        }
    }


    fun searchAppointmentList(page: Int, category: String = "all", keyword: String, start: String? = null, end: String? = null){
        var lastPage: Boolean
        var element: Int

        showProgress()
        viewModelScope.launch {
            val response = repository.searchAppointmentList(page, category, keyword, start, end)
            when(response) {
                is NetworkResponse.Success -> {
                    element = response.body.element
                    lastPage = response.body.last
                    if(!lastPage||(element != 0 && lastPage)){
                        for (document in response.body.boardList) {
                            _appointmentList.postValue(response.body)
                        }
                    }
                }
                is NetworkResponse.ApiError -> {
                    _msg.postValue("Api 오류 : 약속 불러오는 것을 실패했습니다.")
                }
                is NetworkResponse.NetworkError -> {
                    _msg.postValue("서버 오류 : 약속 불러오는 것을 실패했습니다.")
                }
                is NetworkResponse.UnknownError -> {
                    _msg.postValue("알 수 없는 오류 : 약속 불러오는 것을 실패했습니다.")
                }
            }
            hideProgress()
        }
    }


    fun setMarkers(zoom: Int, longitude: Double, latitude:Double) {
        showProgress()
        viewModelScope.launch {
            val response = repository.setAppointmentLocationList(zoom, longitude, latitude)
            when(response) {
                is NetworkResponse.Success -> {
                    _location.postValue(response.body.List)
                }
                is NetworkResponse.ApiError -> {
                    _msg.postValue("Api 오류 : 약속 불러오는 것을 실패했습니다.")
                }
                is NetworkResponse.NetworkError -> {
                    _msg.postValue("서버 오류 : 약속 불러오는 것을 실패했습니다.")
                }
                is NetworkResponse.UnknownError -> {
                    _msg.postValue("알 수 없는 오류 : 약속 불러오는 것을 실패했습니다.")
                }
            }
            hideProgress()
        }
    }


//    fun refreshToken(access: String, refresh: String) {
//        val token = Token(access, refresh)
//        showProgress()
//        viewModelScope.launch {
//            ApiModule.apiBob.refreshTokenResponse(token).enqueue(object : Callback<Token> {
//                override fun onResponse(call: Call<Token>, response: Response<Token>) {
//                    when (response.code()) {
//                        200 -> _refreshToken.postValue(response.body())
//                        403 -> _msg.postValue("로그인 실패 : 토큰 리프레쉬")
//                        500 -> _msg.postValue("로그인 실패 : 서버 오류입니다.")
//                        else -> _msg.postValue("자동 로그인에 실패했습니다. ${response.errorBody()?.string()}")
//                    }
//                    hideProgress()
//                }
//
//                override fun onFailure(call: Call<Token>, t: Throwable) {
//                    _msg.postValue("서버에 연결이 되지 않았습니다.")
//                    Log.d(TAG, "ttt: $t")
//                    hideProgress()
//                }
//            })
//        }
//    }


    fun setDataAtFragment(fragment: Fragment, placeName:String, y:Double, x:Double) {
        val bundle = Bundle()
        bundle.putString("placeName", placeName)
        bundle.putDouble("x", x)
        bundle.putDouble("y", y)
        bundle.putBoolean("click", true)

        fragment.arguments = bundle
    }


    fun searchKeywordMap(keyword: String) {
        showProgress()
        ApiModule.apiKakao.getSearchKeyword(API_KEY, keyword).enqueue(object : Callback<SearchKeyword> {
            override fun onResponse(
                call: Call<SearchKeyword>,
                response: Response<SearchKeyword>
            ) {
                when(response.code()) {
                    200 -> _searchKeyword.postValue(response.body())
                    403 -> checkTokenExpiration(response.errorBody()!!.string())
                    500 -> _msg.postValue("서버 오류입니다.")
                    else -> _msg.postValue("오류 : ${response.errorBody()!!.string()}")
                }
                hideProgress()
            }

            override fun onFailure(call: Call<SearchKeyword>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다. 다시 시도해주세요!")
                hideProgress()
            }
        })
    }

    fun checkTokenExpiration(msg: String) {
        val gson = GsonBuilder().create()
        try {
            val error = gson.fromJson<ErrorResponse>(msg, ErrorResponse::class.java)
            _msg.postValue(error.message)
        } catch (e : IOException) {
            return
        }
    }
}