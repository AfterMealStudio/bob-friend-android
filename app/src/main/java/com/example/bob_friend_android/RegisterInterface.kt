package com.example.bob_friend_android

import com.example.bob_friend_android.DataModel.Login
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RegisterInterface {
    @FormUrlEncoded
    @POST("/app_register/")
    fun requestRegister(
        @Field("userid") userid:String,
        @Field("userpw") userpw:String
    ) : Call<Login>

}