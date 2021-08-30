package com.example.bob_friend_android.model

data class User(
    var id: Int = 0,
    var email: String,
    var username: String,
    var birth: String,
    var sex: String,
    var reportCount: Int,
    var activated: String
)