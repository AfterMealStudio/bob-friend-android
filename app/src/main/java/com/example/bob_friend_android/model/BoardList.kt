package com.example.bob_friend_android.model

import com.google.gson.annotations.SerializedName

class BoardList(
    @SerializedName("content")
    val boardList : List<Board>,
    @SerializedName("last")
    val last : Boolean,
    @SerializedName("numberOfElements")
    val element : Int
)