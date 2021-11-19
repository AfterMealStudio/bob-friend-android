package com.example.bob_friend_android.viewmodel

import android.app.Application
import android.content.Context
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.adapter.BoardAdapter
import com.example.bob_friend_android.adapter.SearchAdapter
import com.example.bob_friend_android.model.Board
import com.example.bob_friend_android.model.Locations
import com.example.bob_friend_android.model.SearchKeyword
import com.example.bob_friend_android.model.SearchLocation
import com.example.bob_friend_android.model.*
import com.example.bob_friend_android.network.KakaoAPI
import com.example.bob_friend_android.network.RetrofitBuilder
import com.example.bob_friend_android.view.main.MainActivity
import com.example.bob_friend_android.view.main.MapFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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

    private val _location = MutableLiveData<List<Locations>>()
    val location : MutableLiveData<List<Locations>>
        get() = _location


    fun setList(listPage: Int){
        var lastPage: Boolean
        var element: Int
        RetrofitBuilder.apiBob.getRecruitments(listPage).enqueue(object : Callback<BoardList> {
            override fun onResponse(call: Call<BoardList>, response: Response<BoardList>) {
                if(response.body() != null) {
                    element = response.body()!!.element
                    lastPage = response.body()!!.last
                    if(!lastPage||(element != 0 && lastPage)){
                        for (document in response.body()!!.boardList) {
                            Log.d(TAG, "setList : ${response.body()!!.boardList}")

                            _boardList.postValue(response.body()!!.boardList as ArrayList<Board>?)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<BoardList>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다. 다시 시도해주세요!")
                Log.e(TAG, t.message.toString())
            }
        })
    }


    fun setMarkers() {
        RetrofitBuilder.apiBob.getRecruitmentLocations().enqueue(object : Callback<List<Locations>> {
            override fun onResponse(call: Call<List<Locations>>, response: Response<List<Locations>>) {
                if(response.body() != null) {
                    _location.postValue(response.body()!!)
                }
            }

            override fun onFailure(call: Call<List<Locations>>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다. 다시 시도해주세요!")
                Log.e(TAG, t.message.toString())
            }
        })
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
        RetrofitBuilder.apiKakao.getSearchKeyword(API_KEY, keyword).enqueue(object : Callback<SearchKeyword> {
            override fun onResponse(
                call: Call<SearchKeyword>,
                response: Response<SearchKeyword>
            ) {
                _searchKeyword.postValue(response.body())
            }

            override fun onFailure(call: Call<SearchKeyword>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다. 다시 시도해주세요!")
                Log.w(TAG, "통신 실패: ${t.message}")
            }
        })
    }
}