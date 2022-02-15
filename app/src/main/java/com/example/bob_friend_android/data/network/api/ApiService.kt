package com.example.bob_friend_android.data.network.api


import com.example.bob_friend_android.data.entity.*
import com.example.bob_friend_android.data.network.NetworkResponse
import com.example.bob_friend_android.model.*
import retrofit2.Call
import retrofit2.http.*

interface ApiService {

    //--- LoginViewModel

    //로그인
    @POST("/api/auth/signin")
    suspend fun getLoginResponse(@Body user : Map<String, String>): NetworkResponse<Token, ErrorResponse>

    //토큰 유효성 확인
    @GET("/api/auth/issue")
    suspend fun validateTokenResponse(): NetworkResponse<UserCheck, ErrorResponse>

    //토큰 재발급
    @POST("/api/auth/issue")
    suspend fun refreshTokenResponse(@Body token: Token): NetworkResponse<Token, ErrorResponse>

    @PUT("/api/user/password")
    suspend fun updateUserPasswordResponse(@Body passwordReset: HashMap<String, String>): NetworkResponse<Void, ErrorResponse>
    //---

    //--- ListViewModel

    //약속들조회
    @GET("/api/recruitments?")
    suspend fun getAppointmentListResponse(@Query("page") id:Int,
                        @Query("type") type:String?, @Query("address") address:String?): NetworkResponse<AppointmentList, ErrorResponse>
    //약속들검색
    @GET("/api/recruitments/search?")
    suspend fun searchAppointmentListResponse(@Query("page") id:Int, @Query("category") category:String, @Query("keyword") keyword:String): NetworkResponse<AppointmentList, ErrorResponse>

    @GET("/api/recruitments/search?")
    suspend fun searchAppointmentListTimeLimitResponse(@Query("page") id:Int, @Query("category") category:String, @Query("keyword") keyword:String,
                                               @Query("start") start:String, @Query("end") end:String): NetworkResponse<AppointmentList, ErrorResponse>
    //약속위치조회
    @GET("/api/recruitments/locations?")
    suspend fun getAppointmentLocationsResponse(@Query("zoom") zoom:Int, @Query("longitude") longitude:Double,
                                        @Query("latitude") latitude:Double,): NetworkResponse<LocationList, ErrorResponse>
    //---


    //---UserViewModel
    //회원가입
    @POST("/api/auth/signup")
    suspend fun signUpResponse(@Body user: Map<String, String>): NetworkResponse<User, ErrorResponse>

    //회원탈퇴
    @HTTP(method = "DELETE", path = "http://117.17.102.143:8080/api/user/", hasBody = true)
    suspend fun deleteUserResponse(@Header("Authorization") token:String, @Body password: Map<String, String>): NetworkResponse<Void, ErrorResponse>

    //회원정보 수정
    @PUT("/api/user")
    suspend fun updateUserResponse(@Body userInfo:Map<String, String?>): NetworkResponse<User, ErrorResponse>

    //닉네임 중복확인
    @GET("/api/user/nickname/{nickname}")
    suspend fun getNicknameCheckResponse(@Path("nickname") username : String): NetworkResponse<DuplicatedCheck, ErrorResponse>

    //이메일 중복 확인
    @GET(" /api/user/email/{email}")
    suspend fun getEmailCheckResponse(@Path("email") email : String): NetworkResponse<DuplicatedCheck, ErrorResponse>

    //사용자 정보 가져오기
    @GET("api/user")
    suspend fun getUserInfoResponse(): NetworkResponse<User, ErrorResponse>

    //---

    //---AppointmentViewModel

    //약속잡기
    @POST("/api/recruitments")
    suspend fun createAppointmentResponse(@Body board: Board): NetworkResponse<Board, ErrorResponse>

    //약속참가
    @PATCH("/api/recruitments/{recruitmentId}")
    suspend fun joinAppointmentResponse(@Path("recruitmentId") recruitmentId: Int): NetworkResponse<Board, ErrorResponse>

    //약속신고
    @PATCH("/api/recruitments/{recruitmentId}/report")
    suspend fun reportAppointmentResponse(@Path("recruitmentId") recruitmentId: Int): NetworkResponse<Void, ErrorResponse>

    //약속마감
    @PATCH("/api/recruitments/{recruitmentId}/close")
    suspend fun closeAppointmentResponse(@Path("recruitmentId") recruitmentId: Int): NetworkResponse<Void, ErrorResponse>

    //약속조회
    @GET("/api/recruitments/{id}")
    suspend fun getAppointmentResponse(@Path("id") id : Int): NetworkResponse<Board, ErrorResponse>

    //약속삭제
    @DELETE("/api/recruitments/{id}")
    suspend fun deleteAppointmentResponse(@Path("id") boardId: Int): NetworkResponse<Void, ErrorResponse>

    //--------------------------

    //댓글 작성
    @POST("/api/recruitments/{recruitmentId}/comments")
    suspend fun createCommentResponse(@Path("recruitmentId") recruitmentId: Int, @Body comment: Map<String, String>): NetworkResponse<Comment, ErrorResponse>

    //댓글 신고
    @PATCH("/api/recruitments/{recruitmentId}/comments/{commentId}/report")
    suspend fun reportCommentResponse(@Path("recruitmentId") recruitmentId: Int, @Path("commentId") commentsId: Int): NetworkResponse<Void, ErrorResponse>

    //댓글 삭제
    @DELETE("/api/recruitments/{recruitmentId}/comments/{commentId}")
    suspend fun deleteCommentResponse(@Path("recruitmentId") recruitmentId: Int, @Path("commentId") commentsId: Int): NetworkResponse<Void, ErrorResponse>

    //대댓글 작성
    @POST("/api/recruitments/{recruitmentId}/comments/{commentId}/replies")
    suspend fun createReCommentResponse(@Path("recruitmentId") recruitmentId: Int, @Path("commentId") commentsId: Int, @Body recomment: Map<String, String>): NetworkResponse<Comment, ErrorResponse>

    //대댓글 신고
    @PATCH("/api/recruitments/{recruitmentId}/comments/{commentId}/replies/{replies}/report")
    suspend fun reportReCommentResponse(@Path("recruitmentId") recruitmentId: Int, @Path("commentId") commentsId: Int, @Path("replies") recommentsId: Int): NetworkResponse<Void, ErrorResponse>

    //대댓글 삭제
    @DELETE("/api/recruitments/{recruitmentId}/comments/{commentId}/replies/{replies}")
    suspend fun deleteReCommentResponse(@Path("recruitmentId") recruitmentId: Int, @Path("commentId") commentsId: Int, @Path("replies") recommentsId: Int): NetworkResponse<Void, ErrorResponse>

    //---
}