package com.example.bob_friend_android.network


import com.example.bob_friend_android.model.Board
import com.example.bob_friend_android.model.Locations
import com.example.bob_friend_android.model.Token
import com.example.bob_friend_android.model.User
import com.example.bob_friend_android.model.*
import retrofit2.Call
import retrofit2.http.*

interface API {

    //회원가입
    @POST("api/signup")
    fun getJoinResponse(@Body user: Map<String, String>): Call<User>

    //닉네임 중복확인
    @GET("api/nickname/{nickname}")
    fun getNicknameCheck(@Path("nickname") username : String): Call<Boolean>

    //이메일 중복 확인
    @GET("api/email/{email}")
    fun getEmailCheck(@Path("email") email : String): Call<Boolean>

    //로그인
    @POST("api/signin")
    fun getLoginResponse(@Body user : Map<String, String>): Call<Token>

    //사용자 정보 가져오기
    @GET("api/user")
    fun getUserId(): Call<User>

    //토큰 가져오기
    @GET("api/validate")
    fun getToken(): Call<Boolean>

    //회원탈퇴
    @DELETE("api/user/{id}")
    fun deleteUser(): Call<Void>

    //약속잡기
    @POST("/recruitments")
    fun addRecruitments(@Body board: Board): Call<Board>

    //약속참가
    @PATCH("/recruitments/{recruitmentId}")
    fun participateBoard(@Path("recruitmentId") recruitmentId: Int): Call<Board>

    //약속들조회
    @GET("/recruitments?")
    fun getRecruitments(@Query("page") id:Int): Call<BoardList>

    //약속하나조회
    @GET("/recruitments/{id}")
    fun getRecruitment(@Path("id") id : Int): Call<Board>

    //약속위치조회
    @GET("/recruitments/locations")
    fun getRecruitmensLocations(): Call<List<Locations>>

    //내 참가약속조회
    @GET("/recruitments/my/joined")
    fun getJoinRecruitmens(): Call<List<Board>>

    //내가 잡은 약속
    @GET("/recruitments/my")
    fun getMyRecruitmens(): Call<List<Board>>

    //약속삭제
    @DELETE("/recruitments/{id}")
    fun deleteRecruitmens(@Path("id") boardId: Int): Call<Void>

    //댓글---------------------------

    //댓글 조회
    @GET("/recruitments/{recruitmentId}/comments")
    fun getComments(@Path("recruitmentId") recruitmentId: Int): Call<List<Comment>>

    @POST("/recruitments/{recruitmentId}/comments")
    fun addComment(@Path("recruitmentId") recruitmentId: Int, @Body comment: Map<String, String>): Call<Comment>

    @PATCH("/recruitments/{recruitmentId}/comments/{commentsId}/report")
    fun deleteComment(@Path("recruitmentId") recruitmentId: Int, @Path("commentsId") commentsId: Int): Call<Void>
}