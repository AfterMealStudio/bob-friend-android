package com.example.bob_friend_android.data.repository.login

import com.example.bob_friend_android.data.entity.ErrorResponse
import com.example.bob_friend_android.data.entity.Token
import com.example.bob_friend_android.data.entity.UserCheck
import com.example.bob_friend_android.data.network.NetworkResponse

interface LoginRepository {
    suspend fun signIn(user: HashMap<String, String>): NetworkResponse<Token, ErrorResponse>
    suspend fun refreshToken(token: Token): NetworkResponse<Token, ErrorResponse>
    suspend fun validateToken() : NetworkResponse<UserCheck, ErrorResponse>
}