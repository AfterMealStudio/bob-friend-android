package com.example.bob_friend_android.viewmodel

import android.app.Application
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bob_friend_android.base.BaseViewModel
import com.example.bob_friend_android.model.Board
import com.example.bob_friend_android.model.Location
import com.example.bob_friend_android.model.SearchKeyword
import com.example.bob_friend_android.model.*
import com.example.bob_friend_android.network.RetrofitBuilder
import com.google.gson.GsonBuilder
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ListViewModel: BaseViewModel() {

    val TAG = "ListViewModel"
    val API_KEY = "KakaoAK 81e4657cca25cf97b1cec85102769390"

    private val _boardList = MutableLiveData<BoardList>()
    val boardList : MutableLiveData<BoardList>
        get() = _boardList

    private val _msg = MutableLiveData<String>()
    val errorMsg : LiveData<String>
        get() = _msg

    private val _searchKeyword = MutableLiveData<SearchKeyword>()
    val searchKeyword : LiveData<SearchKeyword>
        get() = _searchKeyword

    private val _location = MutableLiveData<List<Location>>()
    val location : MutableLiveData<List<Location>>
        get() = _location

    private val _progressVisible = MutableLiveData<Boolean>()
    val progressVisible : LiveData<Boolean>
        get() = _progressVisible

    private val _refreshToken = MutableLiveData<Token>()
    val refreshToken : LiveData<Token>
        get() = _refreshToken



    fun setList(listPage: Int, type: String? = null, address: String? = null){
        var lastPage: Boolean
        var element: Int
        _progressVisible.postValue(true)
        RetrofitBuilder.apiBob.getRecruitments(listPage, type, address).enqueue(object : Callback<BoardList> {
            override fun onResponse(call: Call<BoardList>, response: Response<BoardList>) {
                Log.d(TAG, "setList : ${response.body()}")

                when(response.code()) {
                    200 -> {
                        element = response.body()!!.element
                        lastPage = response.body()!!.last
                        if(!lastPage||(element != 0 && lastPage)){
                            for (document in response.body()!!.boardList) {
                                Log.d(TAG, "setList : ${response.body()!!.boardList}")
                                _boardList.postValue(response.body())
                            }
                        }
                    }
                    403 -> checkTokenExpiration(response.errorBody()!!.string())
                    500 -> _msg.postValue("서버 오류입니다.")
                    else -> _msg.postValue("오류 : ${response.errorBody()!!.string()}")
                }

                _progressVisible.postValue(false)
            }

            override fun onFailure(call: Call<BoardList>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다. 다시 시도해주세요!")
                Log.e(TAG, t.message.toString())
            }
        })
    }


    fun searchList(listPage: Int, category: String = "all", keyword: String, start: String? = null, end: String? = null){
        var lastPage: Boolean
        var element: Int

        _progressVisible.postValue(true)
        if (start != null && end != null) {
            RetrofitBuilder.apiBob.searchRecruitmentsTimeLimit(listPage, category, keyword, start, end).enqueue(object : Callback<BoardList> {
                override fun onResponse(call: Call<BoardList>, response: Response<BoardList>) {
                    Log.d(TAG, "setList : ${response.body()}")

                    when(response.code()) {
                        200 -> {
                            element = response.body()!!.element
                            lastPage = response.body()!!.last
                            if(!lastPage||(element != 0 && lastPage)){
                                for (document in response.body()!!.boardList) {
                                    Log.d(TAG, "setList : ${response.body()!!.boardList}")
                                    _boardList.postValue(response.body())
                                }
                            }
                        }
                        403 -> checkTokenExpiration(response.errorBody()!!.string())
                        500 -> _msg.postValue("서버 오류입니다.")
                        else -> _msg.postValue("오류 : ${response.errorBody()!!.string()}")
                    }

                    _progressVisible.postValue(false)
                }

                override fun onFailure(call: Call<BoardList>, t: Throwable) {
                    _msg.postValue("서버에 연결이 되지 않았습니다. 다시 시도해주세요!")
                    Log.e(TAG, t.message.toString())
                }
            })
        }
        else {
            RetrofitBuilder.apiBob.searchRecruitments(listPage, category, keyword).enqueue(object : Callback<BoardList> {
                override fun onResponse(call: Call<BoardList>, response: Response<BoardList>) {
                    Log.d(TAG, "setList : ${response.body()}")

                    when(response.code()) {
                        200 -> {
                            element = response.body()!!.element
                            lastPage = response.body()!!.last
                            if(!lastPage||(element != 0 && lastPage)){
                                for (document in response.body()!!.boardList) {
                                    Log.d(TAG, "setList : ${response.body()!!.boardList}")
                                    _boardList.postValue(response.body())
                                }
                            }
                        }
                        403 -> checkTokenExpiration(response.errorBody()!!.string())
                        500 -> _msg.postValue("서버 오류입니다.")
                        else -> _msg.postValue("오류 : ${response.errorBody()!!.string()}")
                    }

                    _progressVisible.postValue(false)
                }

                override fun onFailure(call: Call<BoardList>, t: Throwable) {
                    _msg.postValue("서버에 연결이 되지 않았습니다. 다시 시도해주세요!")
                    Log.e(TAG, t.message.toString())
                }
            })
        }
    }


    fun setMarkers(zoom: Int, longitude: Double, latitude:Double) {
        _progressVisible.postValue(true)
        RetrofitBuilder.apiBob.getRecruitmentLocations(zoom, longitude, latitude).enqueue(object : Callback<LocationList> {
            override fun onResponse(call: Call<LocationList>, response: Response<LocationList>) {
                if(response.body() != null) {
                    _location.postValue(response.body()!!.List)
                }

                _progressVisible.postValue(false)
            }

            override fun onFailure(call: Call<LocationList>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다. 다시 시도해주세요!")
                _progressVisible.postValue(false)
                Log.e(TAG, t.message.toString())
            }
        })
    }


    fun refreshToken(access: String, refresh: String) {
        val token = Token(access, refresh)
        _progressVisible.postValue(true)
        viewModelScope.launch {
            RetrofitBuilder.apiBob.refreshToken(token).enqueue(object : Callback<Token> {
                override fun onResponse(call: Call<Token>, response: Response<Token>) {
                    when (response.code()) {
                        200 -> _refreshToken.postValue(response.body())
                        403 -> _msg.postValue("로그인 실패 : 토큰 리프레쉬")
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


    fun setDataAtFragment(fragment: Fragment, placeName:String, y:Double, x:Double) {
        val bundle = Bundle()
        bundle.putString("placeName", placeName)
        bundle.putDouble("x", x)
        bundle.putDouble("y", y)
        bundle.putBoolean("click", true)

        fragment.arguments = bundle
    }


    fun searchKeywordMap(keyword: String) {
        _progressVisible.postValue(true)
        RetrofitBuilder.apiKakao.getSearchKeyword(API_KEY, keyword).enqueue(object : Callback<SearchKeyword> {
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
                _progressVisible.postValue(false)
            }

            override fun onFailure(call: Call<SearchKeyword>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다. 다시 시도해주세요!")
                Log.w(TAG, "통신 실패: ${t.message}")
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