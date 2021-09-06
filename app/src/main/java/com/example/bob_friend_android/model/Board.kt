package com.example.bob_friend_android.model

data class Board (
    var id : Int = 0,
    var title : String = "",
    var content : String = "",
    var author : String = "",
    var member : List<User>? = null,
    var currentNumberOfPeople: Int = 1,
    var totalNumberOfPeople: Int? = null,
    var full: Boolean? = null,
    var restaurantName: String? = "",
    var restaurantAddress: String? = "",
    var latitude:Double? = 0.0,
    var longitude:Double? = 0.0,
    var appointmentTime: String? = null,
    var createdAt: String? = null,
    var report: Long? = null
)