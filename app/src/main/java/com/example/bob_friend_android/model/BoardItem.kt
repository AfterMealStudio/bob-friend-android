package com.example.bob_friend_android.model

import android.os.Parcel
import android.os.Parcelable

class BoardItem(var title: String?, var content: String?, var username: String?, var currentNumberOfPeople: Int, var totalNumberOfPeople: Int, var createdAt: String?, var location: String?) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeString(username)
        parcel.writeInt(currentNumberOfPeople)
        parcel.writeInt(totalNumberOfPeople)
        parcel.writeString(createdAt)
        parcel.writeString(location)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BoardItem> {
        override fun createFromParcel(parcel: Parcel): BoardItem {
            return BoardItem(parcel)
        }

        override fun newArray(size: Int): Array<BoardItem?> {
            return arrayOfNulls(size)
        }
    }
}