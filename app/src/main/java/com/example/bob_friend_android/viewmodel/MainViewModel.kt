package com.example.bob_friend_android.viewmodel

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import com.example.bob_friend_android.App
import com.example.bob_friend_android.network.KakaoAPI
import com.example.bob_friend_android.adapter.SearchAdapter
import com.example.bob_friend_android.model.SearchKeyword
import com.example.bob_friend_android.model.SearchLocation
import com.example.bob_friend_android.model.User
import com.example.bob_friend_android.network.RetrofitBuilder
import com.example.bob_friend_android.view.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val TAG = "MainViewModel"

    fun setUserInfo(headerUserName:TextView, headerEmail:TextView) {
        var nickname: String
        var email: String
        val token = App.prefs.getString("token", "")
        Log.d(TAG,"token: $token")

        RetrofitBuilder.api.getUserId(token).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                nickname = response.body()?.nickname.toString()
                email = response.body()?.email.toString()
                Log.d(TAG,"responese: $response, username: $nickname, email: $email")
                headerUserName.text = nickname
                headerEmail.text = email
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e(TAG, t.message.toString())
            }
        })
    }


    fun searchKeyword(keyword: String, searchAdapter:SearchAdapter,context: Context) {
        val retrofit = Retrofit.Builder()   // Retrofit 구성
            .baseUrl(MainActivity.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)   // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(MainActivity.API_KEY, keyword)   // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object: Callback<SearchKeyword> {
            override fun onResponse(
                call: Call<SearchKeyword>,
                response: Response<SearchKeyword>
            ) {
                // 통신 성공 (검색 결과는 response.body()에 담겨있음)
                Log.d(TAG, "Raw: ${response.raw()}")
                Log.d(TAG, "Body: ${response.body()}")
                addItems(response.body(), context, searchAdapter)
            }

            override fun onFailure(call: Call<SearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w(TAG, "통신 실패: ${t.message}")
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

        Log.d(TAG, "x:${x}, y:${y}")
    }


    fun addItems(searchResult: SearchKeyword?, context:Context, adapter:SearchAdapter) {
        val listItems: ArrayList<SearchLocation> = adapter.getItems()

        if (!searchResult?.documents.isNullOrEmpty()) { // 검색 결과 있음
            listItems.clear()
            for (document in searchResult!!.documents) {
                // 결과를 리사이클러 뷰에 추가
                val item = SearchLocation(document.place_name,
                    document.road_address_name,
                    document.address_name,
                    document.x.toDouble(),
                    document.y.toDouble())
                listItems.add(item)}

            adapter.notifyDataSetChanged()
        } else { // 검색 결과 없음
            Toast.makeText(context, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }
}