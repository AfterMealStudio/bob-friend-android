package com.example.bob_friend_android.data.entity

import android.os.Parcel
import android.os.Parcelable

data class SearchLocation(
    val name: String?,      // 장소명
    val road: String?,      // 도로명 주소
    val address: String?,   // 지번 주소
    val x: Double,         // 경도(Longitude)
    val y: Double) : Parcelable       // 위도(Latitude)
{
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(road)
        parcel.writeString(address)
        parcel.writeDouble(x)
        parcel.writeDouble(y)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SearchLocation> {
        override fun createFromParcel(parcel: Parcel): SearchLocation {
            return SearchLocation(parcel)
        }

        override fun newArray(size: Int): Array<SearchLocation?> {
            return arrayOfNulls(size)
        }
    }
}
