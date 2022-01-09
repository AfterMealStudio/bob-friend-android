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

class ListViewModel(application: Application) : AndroidViewModel(application) {

    val TAG = "ListViewModel"
    val API_KEY = "KakaoAK 81e4657cca25cf97b1cec85102769390"

    private val _boardList = MutableLiveData<ArrayList<Board>>()
    val boardList : MutableLiveData<ArrayList<Board>>
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



    fun setList(listPage: Int, keyword: String? = null, type: String? = null, start: String? = null, end: String? = null, address: String? = null){
        var lastPage: Boolean
        var element: Int
        _progressVisible.postValue(true)
        RetrofitBuilder.apiBob.getRecruitments(listPage, keyword, type, start, end, address).enqueue(object : Callback<BoardList> {
            override fun onResponse(call: Call<BoardList>, response: Response<BoardList>) {
                Log.d(TAG, "setList : ${response.body()}")

                if(response.code() == 403) {
                    val gson = GsonBuilder().create()
                    try {
                        val error = gson.fromJson<ErrorResponse>(response.errorBody()!!.string(), ErrorResponse::class.java)
                        if (error.message == null){
                            _msg.postValue(error.error)
                        }
                        else {
                            _msg.postValue(error.message)
                        }
                    } catch (e : IOException) {
                        return
                    }
                }

                if(response.body() != null) {
                    element = response.body()!!.element
                    lastPage = response.body()!!.last
                    if(!lastPage||(element != 0 && lastPage)){
                        for (document in response.body()!!.boardList) {
                            Log.d(TAG, "setList : ${response.body()!!.boardList}")
                            _boardList.postValue(response.body()!!.boardList as ArrayList<Board>?)
                        }
                    }
                    else if (keyword != null){
                        _msg.postValue("검색 결과가 없습니다.")
                    }
                }

                _progressVisible.postValue(false)
            }

            override fun onFailure(call: Call<BoardList>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다. 다시 시도해주세요!")
                Log.e(TAG, t.message.toString())
            }
        })
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
                        500 -> _msg.postValue("로그인 실패 : 서버 오류입니다.")
                        else -> _msg.postValue("자동 로그인에 실패했습니다. ${response.errorBody()?.toString()}")
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
                _searchKeyword.postValue(response.body())
                _progressVisible.postValue(false)
            }

            override fun onFailure(call: Call<SearchKeyword>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다. 다시 시도해주세요!")
                Log.w(TAG, "통신 실패: ${t.message}")
            }
        })
    }
}