package com.example.bob_friend_android.model

data class Board (
    val boardTitle : String?,
    val boardContent : String?,
    val userName : String?,
//    val author : User?,
//    val member : List<User>?,
//    val currentNumberOfParticipants: Int?,
//    val totalNumberOfParticipants: Int?,
//    val full: Boolean?,
//    val restaurantName: String?,
//    val latitude:Double?,
//    val longitude:Double?,
    //val currentNumberOfComments: Int,
//    val startAt: String?,
//    val endAt: String?,
//    var report: Long?
){
    override fun toString(): String {
        return "Board(boardTitle=$boardTitle, boardContent=$boardContent, userName=$userName)"
    }
}