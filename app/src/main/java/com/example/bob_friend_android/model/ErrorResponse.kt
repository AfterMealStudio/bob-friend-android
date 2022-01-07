package com.example.bob_friend_android.model

import com.google.gson.annotations.SerializedName


class ErrorResponse (
    @SerializedName("message")
    val message : String?,
    @SerializedName("error")
    val error : String
)