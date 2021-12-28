package com.example.bob_friend_android.model

import com.google.gson.annotations.SerializedName

data class LocationList (
    @SerializedName("addresses")
    var List: List<Location>
        )

data class Location(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var address: String = "",
    var count: Int = 1
)