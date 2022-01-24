package com.example.bob_friend_android.data.repository.user

import com.example.bob_friend_android.data.entity.DuplicatedCheck
import com.example.bob_friend_android.data.entity.ErrorResponse
import com.example.bob_friend_android.data.entity.User
import com.example.bob_friend_android.data.network.NetworkResponse
import com.example.bob_friend_android.data.network.api.ApiService
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : UserRepository {
    override suspend fun signUp(user: HashMap<String, String>): NetworkResponse<User, ErrorResponse> {
        return apiService.signUpResponse(user)
    }

    override suspend fun deleteUser(
        token: String,
        password: HashMap<String, String>
    ): NetworkResponse<Void, ErrorResponse> {
        return apiService.deleteUserResponse(token, password)
    }

    override suspend fun updateUser(updateUserInfo: HashMap<String, String?>): NetworkResponse<User, ErrorResponse> {
        return apiService.updateUserResponse(updateUserInfo)
    }

    override suspend fun setUserInfo(): NetworkResponse<User, ErrorResponse> {
        return apiService.getUserInfoResponse()
    }

    override suspend fun checkUserNickname(userId: String): NetworkResponse<DuplicatedCheck, ErrorResponse> {
        return apiService.getNicknameCheckResponse(userId)
    }

    override suspend fun checkUserEmail(email: String): NetworkResponse<DuplicatedCheck, ErrorResponse> {
        return apiService.getEmailCheckResponse(email)
    }

}