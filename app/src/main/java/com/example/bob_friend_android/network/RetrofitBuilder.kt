package com.example.bob_friend_android.network

import com.example.bob_friend_android.App
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

object RetrofitBuilder {
    var api: API

    val gson = GsonBuilder()
        .setLenient()
        .create()

    init {
        val client = OkHttpClient.Builder()
        client.addInterceptor {
            val request = it.request().newBuilder().addHeader("Authorization", App.prefs.getString("token", "")!!).build()
            it.proceed(request)
        }

        val retrofit = Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client.build())
            .baseUrl("http://117.17.102.143:8080")
            .build()
        api = retrofit.create(API::class.java)
    }
}