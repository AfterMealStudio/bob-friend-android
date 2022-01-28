package com.example.bob_friend_android.model

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class LocationList (
    @SerializedName("addresses")
    var List: List<Location>
        )

data class Location(
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var address: String? = "",
    var count: Int = 1
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeString(address)
        parcel.writeInt(count)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Location> {
        override fun createFromParcel(parcel: Parcel): Location {
            return Location(parcel)
        }

        override fun newArray(size: Int): Array<Location?> {
            return arrayOfNulls(size)
        }
    }
}