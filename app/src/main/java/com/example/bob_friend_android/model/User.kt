package com.example.bob_friend_android.model

data class User(
    var id: Int = 0,
    var userId: String,
    var nickname: String,
    var email: String,
    var birth: String,
    var sex: String,
    var reportCount: Int,
    var agree: Boolean,
    var activated: String
)