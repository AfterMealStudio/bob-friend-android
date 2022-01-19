package com.example.bob_friend_android.data.entity

import com.example.bob_friend_android.data.entity.Board
import com.google.gson.annotations.SerializedName

class BoardList(
    @SerializedName("content")
    val boardList : List<Board>,
    @SerializedName("last")
    val last : Boolean,
    @SerializedName("numberOfElements")
    val element : Int,
    @SerializedName("totalElements")
    val totalElements : Int
)