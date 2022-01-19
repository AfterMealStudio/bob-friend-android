package com.example.bob_friend_android.data.entity

import com.google.gson.annotations.SerializedName

data class Token(
    @SerializedName("accessToken")
    val accessToken: String,
    @SerializedName("refreshToken")
    val refreshToken: String
)