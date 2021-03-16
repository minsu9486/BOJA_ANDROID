package com.example.android.navigationadvancedsample.homescreen

import android.graphics.Color
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.navigationadvancedsample.MainActivity
import com.example.android.navigationadvancedsample.R
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.squareup.picasso.Picasso
import org.json.JSONObject

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

    private val picasso: Picasso = Picasso.get()

    class CardViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, itemType: Int): CardViewHolder {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.card_view_item, viewGroup, false)
        return CardViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: CardViewHolder, position: Int) {

        val movieTitle = myDataset[position % itemCount]?.title?: "NoImage"
        viewHolder.view.findViewById<AppCompatTextView>(R.id.card_movie_title).text = movieTitle

        // https://docs.microsoft.com/en-us/bing/search-apis/bing-image-search/reference/query-parameters
        val searchOptions = "&count=1" + "&aspect=Tall" + "&size=Medium"

//        (viewHolder.view.context as MainActivity).setProgressIndicator(viewHolder.view, true)

        // https://docs.microsoft.com/en-us/bing/search-apis/bing-image-search/reference/endpoints
        (viewHolder.view.context.getString(R.string.Azure_Search_URL) + "?q=" + movieTitle + searchOptions)
                .httpGet()
                .header("Ocp-Apim-Subscription-Key", viewHolder.view.context.getString(R.string.Azure_key))
                .responseString { _, _, result ->
                    when (result) {
                        is Result.Failure -> {
//                            val data = result
                            println(result)

//                            (viewHolder.view.context as MainActivity).setProgressIndicator(viewHolder.view, false)
                        }
                        is Result.Success -> {
                            val data = result.get()
                            Log.v("CardMovie", data)

//                            (viewHolder.view.context as MainActivity).setProgressIndicator(viewHolder.view, false)

                            // https://docs.microsoft.com/en-us/bing/search-apis/bing-image-search/reference/response-objects#imageanswer
                            val imageData = JSONObject(data).getJSONArray("value").getJSONObject(0);
                            // https://docs.microsoft.com/en-us/bing/search-apis/bing-image-search/reference/response-objects#image
                            val thumbnailUrl = imageData.getString("thumbnailUrl"); // contentUrl
                            val accentColor = imageData.getString("accentColor");

                            viewHolder.view.findViewById<ImageView>(R.id.card_movie_image).setBackgroundColor(Color.parseColor("#$accentColor"))

                            picasso.load(thumbnailUrl)
//                                    .placeholder(R.drawable.movie_post_0) // replaced "movie_post_0" to the given "accentColor"
                                    .error(R.drawable.movie_post_error)
                                    .into(viewHolder.view.findViewById<ImageView>(R.id.card_movie_image))

//                            viewHolder.view.findViewById<ImageView>(R.id.card_movie_image)
//                                    .setImageResource(listOfColdMovie[position % itemCount])
                        }
                    }
                }

//        viewHolder.view.findViewById<ImageView>(R.id.card_movie_image)
//                .setImageResource(listOfColdMovie[position % itemCount])
////                .setImageResource(R.drawable.movie_post_0)
//
//        viewHolder.view.findViewById<AppCompatTextView>(R.id.card_movie_title).text = movieTitle
    }

    override fun getItemCount(): Int {
        return itemCount
    }
}
//
//private val listOfColdMovie = listOf(
//        R.drawable.movie_post_cold_1,
//        R.drawable.movie_post_cold_2,
//        R.drawable.movie_post_cold_3,
//        R.drawable.movie_post_cold_4,
//        R.drawable.movie_post_cold_5,
//        R.drawable.movie_post_cold_6,
//        R.drawable.movie_post_cold_7,
//        R.drawable.movie_post_cold_8,
//        R.drawable.movie_post_cold_9,
//        R.drawable.movie_post_cold_10
//)