package com.example.bob_friend_android.data.repository.login

import com.example.bob_friend_android.data.entity.ErrorResponse
import com.example.bob_friend_android.data.entity.Token
import com.example.bob_friend_android.data.entity.UserCheck
import com.example.bob_friend_android.data.network.NetworkResponse
import com.example.bob_friend_android.data.network.api.ApiService
import javax.inject.Inject

class LoginRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : LoginRepository {
    override suspend fun signIn(user: HashMap<String, String>): NetworkResponse<Token, ErrorResponse> {
        return apiService.getLoginResponse(user)
    }

    override suspend fun refreshToken(token: Token): NetworkResponse<Token, ErrorResponse> {
        return apiService.refreshTokenResponse(token)
    }

    override suspend fun validateToken(): NetworkResponse<UserCheck, ErrorResponse> {
        return apiService.validateTokenResponse()
    }
}