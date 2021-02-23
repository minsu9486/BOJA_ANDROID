package com.example.android.navigationadvancedsample.homescreen

import android.os.Parcel
import android.os.Parcelable

data class CardMovie(var index: Int?, var id: String?, var title: String?, var genre: String?): Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readValue(Int::class.java.classLoader) as? Int,
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(index)
        parcel.writeValue(id)
        parcel.writeString(title)
        parcel.writeString(genre)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CardMovie> {
        override fun createFromParcel(parcel: Parcel): CardMovie {
            return CardMovie(parcel)
        }

        override fun newArray(size: Int): Array<CardMovie?> {
            return arrayOfNulls(size)
        }
    }
}