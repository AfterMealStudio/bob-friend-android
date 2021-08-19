package com.example.bob_friend_android.DataModel

data class Board (
    val boardId : Long,
    val boardTitle : String,
    val boardContent : String,
    val userName : String,
    val currentNumberOfParticipants: Int,
    val totalNumberOfParticipants: Int,
    val currentNumberOfComments: Int,
    val createDate: String,
    var report: Long?
){
    override fun toString(): String {
        return "Board(boardId=$boardId, boardTitle=$boardTitle, boardContent=$boardContent, userName=$userName," +
                " currentNumberOfParticipants=$currentNumberOfParticipants, totalNumberOfParticipants=$totalNumberOfParticipants," +
                " currentNumberOfComments=$currentNumberOfComments, createDate=$createDate, report=$report)"
    }
}