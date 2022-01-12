package com.example.bob_friend_android.network

import com.example.bob_friend_android.App
import com.example.bob_friend_android.SharedPref
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitBuilder {
    var apiBob: API
    var apiKakao: KakaoAPI
    var retrofitBob: Retrofit

    val gson = GsonBuilder()
        .setLenient()
        .create()

    init {
        val client = OkHttpClient.Builder()
        client.addInterceptor(AuthInterceptor(SharedPref)).addInterceptor {
            val request = it.request()
            if (request.url().encodedPath().equals("/api/signup", true)
                || request.url().encodedPath().equals("/api/signin", true)
                || request.url().encodedPath().equals("/api/issue", true)
            ) {
                it.proceed(request)
            } else {
                it.proceed(request.newBuilder().apply {
                    addHeader("Authorization", App.prefs.getString("token", "")!!)
                }.build())
            }
        }.build()

        retrofitBob = Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client.build())
            .baseUrl("http://117.17.102.143:8080")
            .build()

        apiBob = retrofitBob.create(API::class.java)

        val retrofitKakao = Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiKakao = retrofitKakao.create(KakaoAPI::class.java)
    }
}