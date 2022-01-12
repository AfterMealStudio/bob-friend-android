package com.example.bob_friend_android.network

import android.util.Log
import com.example.bob_friend_android.SharedPref
import com.example.bob_friend_android.model.Token
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Call
import retrofit2.Callback

internal class AuthInterceptor(
//    private val api: API,
    private val sharedPrefs: SharedPref
    ) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request();
        val response = chain.proceed(request);

        when (response.code()) {
            400 -> {
                //Show Bad Request Error Message
            }
            401 -> {
                //Show UnauthorizedError Message
            }

            403 -> {
//                api.refreshToken(sharedPrefs.getToken()!!).enqueue(object : Callback<Token> {
//                    override fun onResponse(call: Call<Token>, response: retrofit2.Response<Token>) {
//                        if (response.code() == 200) {
//                            val newToken = response.body()
//                            if (newToken != null) {
//                                sharedPrefs.saveToken(newToken)
//                            }
//                        }
//                    }
//
//                    override fun onFailure(call: Call<Token>, t: Throwable) {
//                        Log.d("AuthInterceptor", "ttt: $t")
//                    }
//                })
                //Show Forbidden Message
            }

            404 -> {
                //Show NotFound Message
            }
        }
        return response
    }
}