package com.example.bob_friend_android.network

import com.example.bob_friend_android.App
import com.example.bob_friend_android.view.main.MainActivity
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitBuilder {
    var apiBob: API
    var apiKakao: KakaoAPI

    val gson = GsonBuilder()
        .setLenient()
        .create()

    init {
        val client = OkHttpClient.Builder()
        client.addInterceptor {
            val request = it.request().newBuilder().addHeader("Authorization", App.prefs.getString("token", "")!!).build()
            it.proceed(request)
        }

        val retrofitBob = Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client.build())
            .baseUrl("http://117.17.102.143:8080")
            .build()

        apiBob = retrofitBob.create(API::class.java)

        val retrofitKakao = Retrofit.Builder()   // Retrofit 구성
            .baseUrl("https://dapi.kakao.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiKakao = retrofitKakao.create(KakaoAPI::class.java)   // 통신 인터페이스를 객체로 생성
    }
}