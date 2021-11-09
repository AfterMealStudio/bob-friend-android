package com.example.bob_friend_android.model

import android.os.Parcel
import android.os.Parcelable
import java.util.ArrayList


class BoardItem(
    var id: Int, var title: String?, var content: String?, var username: String?, var currentNumberOfPeople: Int,
    var totalNumberOfPeople: Int, var appointmentTime: String?, var location: String?, var x: Double, var y: Double, var accountOfComments: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readInt()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeString(username)
        parcel.writeInt(currentNumberOfPeople)
        parcel.writeInt(totalNumberOfPeople)
        parcel.writeString(appointmentTime)
        parcel.writeString(location)
        parcel.writeDouble(x)
        parcel.writeDouble(y)
        parcel.writeInt(accountOfComments)
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