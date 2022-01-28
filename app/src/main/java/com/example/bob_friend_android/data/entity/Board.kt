package com.example.bob_friend_android.data.entity

data class Board (
    var id : Int = 0,
    var title : String = "",
    var content : String = "",
    var author : User? = null,
    var members : List<UserItem>? = null,
    var comments : List<Comment>? = null,
    var amountOfComments: Int = 0,
    var currentNumberOfPeople: Int = 1,
    var totalNumberOfPeople: Int? = null,
    var full: Boolean? = null,
    var restaurantName: String? = "",
    var restaurantAddress: String? = "",
    var latitude:Double? = 0.0,
    var longitude:Double? = 0.0,
    var appointmentTime: String? = null,
    var createdAt: String? = null,
    var sexRestriction: String = "",
    var report: Long? = null,
    var ageRestrictionStart: Int? = null,
    var ageRestrictionEnd: Int? = null
)