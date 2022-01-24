package com.example.bob_friend_android.data.repository.user

import com.example.bob_friend_android.data.entity.DuplicatedCheck
import com.example.bob_friend_android.data.entity.ErrorResponse
import com.example.bob_friend_android.data.entity.User
import com.example.bob_friend_android.data.network.NetworkResponse

interface UserRepository {
    suspend fun signUp(user: HashMap<String, String>): NetworkResponse<User, ErrorResponse>
    suspend fun deleteUser(token: String, password: HashMap<String, String>): NetworkResponse<Void, ErrorResponse>
    suspend fun updateUser(updateUserInfo: HashMap<String, String?>): NetworkResponse<User, ErrorResponse>
    suspend fun setUserInfo(): NetworkResponse<User, ErrorResponse>
    suspend fun checkUserNickname(userId: String): NetworkResponse<DuplicatedCheck, ErrorResponse>
    suspend fun checkUserEmail(email: String): NetworkResponse<DuplicatedCheck, ErrorResponse>
}