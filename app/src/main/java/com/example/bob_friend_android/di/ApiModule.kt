package com.example.bob_friend_android.di

import com.example.bob_friend_android.App
import com.example.bob_friend_android.data.network.NetworkResponseAdapterFactory
import com.example.bob_friend_android.data.network.api.ApiService
import com.example.bob_friend_android.data.network.api.KakaoApiService
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    private const val kakaoUrl = "https://dapi.kakao.com/"
    private const val baseUrl = "http://117.17.102.143:8080"


    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .addInterceptor {
                val request = it.request()
                if (request.url.encodedPath.equals("/api/signup", true)
                    || request.url.encodedPath.equals("/api/signin", true)
                    || request.url.encodedPath.equals("/api/issue", true)
                ) {
                    it.proceed(request)
                } else {
                    it.proceed(request.newBuilder().apply {
                        addHeader("Authorization", App.prefs.getString("token", "token")!!)
                    }.build())
                }
            }.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(NetworkResponseAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    var apiBob: ApiService
    var apiKakao: KakaoApiService
    var retrofitBob: Retrofit

    val gson = GsonBuilder()
        .setLenient()
        .create()

    init {
        val client = OkHttpClient.Builder()
        client
            .addInterceptor {
            val request = it.request()
            if (request.url.encodedPath.equals("/api/signup", true)
                || request.url.encodedPath.equals("/api/signin", true)
                || request.url.encodedPath.equals("/api/issue", true)
            ) {
                it.proceed(request)
            } else {
                it.proceed(request.newBuilder().apply {
                    addHeader("Authorization", App.prefs.getString("token", "token")!!)
                }.build())
            }
        }.build()

        retrofitBob = Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client.build())
            .baseUrl("http://117.17.102.143:8080")
            .build()

        apiBob = retrofitBob.create(ApiService::class.java)

        val retrofitKakao = Retrofit.Builder()
            .baseUrl("https://dapi.kakao.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiKakao = retrofitKakao.create(KakaoApiService::class.java)
    }
}