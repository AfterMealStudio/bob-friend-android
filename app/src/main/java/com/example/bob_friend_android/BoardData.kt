package com.example.bob_friend_android

data class BoardData (
    val boardTitle : String,
    val boardContent : String,
    val userName : String,
    val currentNumberOfParticipants: Int,
    val totalNumberOfParticipants: Int,
    val currentNumberOfComments: Int,
    val createDate: String
)