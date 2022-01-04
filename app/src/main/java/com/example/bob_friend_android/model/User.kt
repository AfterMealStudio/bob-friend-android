package com.example.bob_friend_android.model

data class User(
    var id: Int = 0,
    var email: String,
    var nickname: String,
    var age: String,
    var sex: String,
    var reportCount: Int?,
    var accumulatedReports: Int?,
    var agree: Boolean,
    var activated: Boolean,
    var rating: Float
)