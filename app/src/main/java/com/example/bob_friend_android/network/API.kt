package com.example.bob_friend_android.network


import com.example.bob_friend_android.model.Board
import com.example.bob_friend_android.model.Token
import com.example.bob_friend_android.model.User
import com.example.bob_friend_android.model.*
import retrofit2.Call
import retrofit2.http.*

interface API {

    //회원가입
    @POST("api/signup")
    fun getJoinResponse(@Body user: Map<String, String>): Call<User>

    //회원탈퇴
    @HTTP(method = "DELETE", path = "http://117.17.102.143:8080/api/user/", hasBody = true)
    fun deleteUser(@Header("Authorization") token:String, @Body password: Map<String, String>): Call<Void>

    //회원정보 수정
    @PUT("/api/user")
    fun updateUser(@Body userInfo:Map<String, String?>): Call<User>

    //닉네임 중복확인
    @GET("api/nickname/{nickname}")
    fun getNicknameCheck(@Path("nickname") username : String): Call<DuplicatedCheck>

    //이메일 중복 확인
    @GET("api/email/{email}")
    fun getEmailCheck(@Path("email") email : String): Call<DuplicatedCheck>

    //로그인
    @POST("api/signin")
    fun getLoginResponse(@Body user : Map<String, String>): Call<Token>

    //사용자 정보 가져오기
    @GET("api/user")
    fun getUserId(): Call<User>

    //토큰 가져오기
    @GET("api/validate")
    fun getToken(): Call<Map<String,Boolean>>

    //토큰 재발급
    @POST("api/reissue")
    fun refreshToken(@Body token: Token): Call<Token>

    //약속잡기
    @POST("/recruitments")
    fun addRecruitments(@Body board: Board): Call<Board>

    //약속참가
    @PATCH("/recruitments/{recruitmentId}")
    fun participateBoard(@Path("recruitmentId") recruitmentId: Int): Call<Board>

    //약속신고
    @PATCH("/recruitments/{recruitmentId}/report")
    fun reportBoard(@Path("recruitmentId") recruitmentId: Int): Call<Void>

    //약속마감
    @PATCH("/recruitments/{recruitmentId}/close")
    fun closeBoard(@Path("recruitmentId") recruitmentId: Int): Call<Void>

    //약속들조회
    @GET("/recruitments?")
    fun getRecruitments(@Query("page") id:Int): Call<BoardList>

    //약속하나조회
    @GET("/recruitments/{id}")
    fun getRecruitment(@Path("id") id : Int): Call<Board>

    //약속위치조회
    @GET("/recruitments/locations?")
    fun getRecruitmentLocations(@Query("zoom") zoom:Int, @Query("longitude") longitude:Double,
                                @Query("latitude") latitude:Double,): Call<LocationList>

    //주소로 약속조희
    @GET("/recruitments?")
    fun getRecruitmentAddress(@Query("type") type:String, @Query("address") address:String): Call<BoardList>

    //내 약속조회
    @GET("/recruitments?")
    fun getMyRecruitment(@Query("type") type:String, @Query("page") page:Int, @Query("sort") sort:String): Call<BoardList>

    //약속검색
    @GET("/recruitments/search?")
    fun searchList(@Query("category") category:String, @Query("keyword") keyword:String) :Call<BoardList>

    //약속검색-시간 제한 기능
    @GET("/recruitments/search?")
    fun searchListTimeLimits(@Query("category") category:String, @Query("keyword") keyword:String, @Query("start") start:String, @Query("end") end:String) :Call<BoardList>

    //약속삭제
    @DELETE("/recruitments/{id}")
    fun deleteRecruitment(@Path("id") boardId: Int): Call<Void>

    //댓글---------------------------

    //댓글 조회
    @GET("/recruitments/{recruitmentId}/comments")
    fun getComments(@Path("recruitmentId") recruitmentId: Int): Call<List<Comment>>

    //댓글 작성
    @POST("/recruitments/{recruitmentId}/comments")
    fun addComment(@Path("recruitmentId") recruitmentId: Int, @Body comment: Map<String, String>): Call<Comment>

    //댓글 신고
    @PATCH("/recruitments/{recruitmentId}/comments/{commentId}/report")
    fun reportComment(@Path("recruitmentId") recruitmentId: Int, @Path("commentId") commentsId: Int): Call<Void>

    //댓글 삭제
    @DELETE("/recruitments/{recruitmentId}/comments/{commentId}")
    fun deleteComment(@Path("recruitmentId") recruitmentId: Int, @Path("commentId") commentsId: Int): Call<Void>

    //대댓글 작성
    @POST("/recruitments/{recruitmentId}/comments/{commentId}/replies")
    fun addReComment(@Path("recruitmentId") recruitmentId: Int, @Path("commentId") commentsId: Int, @Body recomment: Map<String, String>): Call<Comment>

    //대댓글 신고
    @PATCH("/recruitments/{recruitmentId}/comments/{commentId}/replies/{replies}/report")
    fun reportReComment(@Path("recruitmentId") recruitmentId: Int, @Path("commentId") commentsId: Int, @Path("replies") recommentsId: Int): Call<Void>

    //대댓글 삭제
    @DELETE("/recruitments/{recruitmentId}/comments/{commentId}/replies/{replies}")
    fun deleteReComment(@Path("recruitmentId") recruitmentId: Int, @Path("commentId") commentsId: Int, @Path("replies") recommentsId: Int): Call<Void>
}