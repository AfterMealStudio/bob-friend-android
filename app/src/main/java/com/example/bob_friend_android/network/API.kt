package com.example.bob_friend_android.network

import com.example.bob_friend_android.model.Board
import com.example.bob_friend_android.model.Token
import com.example.bob_friend_android.model.User
import retrofit2.Call
import retrofit2.http.*

interface API {

    //회원가입
    @POST("api/signup")
    fun getJoinResponse(@Body user: Map<String, String>): Call<User>

    //로그인
    @POST("api/signin")
    fun getLoginResponse(@Body user : Map<String, String>): Call<Token>

    //사용자 정보 가져오기
    @GET("api/user")
    fun getUserId(@Header("Authorization") token: String): Call<User>

    //회원탈퇴
    @DELETE("api/user/{username}")
    fun deleteUser(@Header("X-AUTH-TOKEN") token: String, @Path("username") id: Int): Call<Void>

    //약속잡기
    @POST("/recruitments")
    fun addRecruitmens(@Header("Authorization") token: String, @Body board: Board): Call<Board>
}