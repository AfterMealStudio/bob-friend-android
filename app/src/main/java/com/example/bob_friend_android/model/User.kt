package com.example.bob_friend_android.model

data class User(
    var id: Int = 0,
    var email: String,
    var nickname: String,
    var birth: String,
    var sex: String,
    var reportCount: Int?,
    var accumulatedReports: Boolean?,
    var agree: Boolean,
    var activated: Boolean
)