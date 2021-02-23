package com.example.android.navigationadvancedsample.homescreen

import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.navigationadvancedsample.R

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

class CardAdapter(private val myDataset: Array<CardMovie?>, private val itemCount: Int) : RecyclerView.Adapter<CardAdapter.CardViewHolder>() {

    class CardViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, itemType: Int): CardViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.card_view_item, viewGroup, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: CardViewHolder, position: Int) {

        viewHolder.view.findViewById<ImageView>(R.id.card_movie_image)
                .setImageResource(listOfColdMovie[position % itemCount])
//                .setImageResource(R.drawable.movie_post_0)

        viewHolder.view.findViewById<AppCompatTextView>(R.id.card_movie_title).text = myDataset[position % itemCount]?.title
                ?: "title uninit"
    }

    override fun getItemCount(): Int {
        return itemCount
    }
}

private val listOfColdMovie = listOf(
        R.drawable.movie_post_cold_1,
        R.drawable.movie_post_cold_2,
        R.drawable.movie_post_cold_3,
        R.drawable.movie_post_cold_4,
        R.drawable.movie_post_cold_5,
        R.drawable.movie_post_cold_6,
        R.drawable.movie_post_cold_7,
        R.drawable.movie_post_cold_8,
        R.drawable.movie_post_cold_9,
        R.drawable.movie_post_cold_10
)