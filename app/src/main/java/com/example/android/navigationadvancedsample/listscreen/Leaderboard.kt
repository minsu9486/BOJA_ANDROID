/*
 * Copyright 2019, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.navigationadvancedsample.listscreen

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.navigationadvancedsample.MainActivity
import com.example.android.navigationadvancedsample.R
import com.example.android.navigationadvancedsample.homescreen.CardMovie
import com.github.kittinunf.fuel.core.extensions.cUrlString
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONObject


/**
 * Shows a static leaderboard with multiple users.
 */
class Leaderboard : Fragment() {

    val TAG = MainActivity::class.java.simpleName

    private var userID = 0
    private var mCards = mutableListOf<CardMovie>(CardMovie(0, "", "", "", ""))

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_leaderboard, container, false)
        val sharedPref = activity?.getSharedPreferences("LoginStatus", Context.MODE_PRIVATE)
        userID = sharedPref?.getInt("user_id", 0)!!

        // first attempt, clear mCards
        if(mCards[mCards.size - 1].id != userID.toString()) {
            mCards = mutableListOf<CardMovie>(CardMovie(0, "", "", "", ""))
            mCards[mCards.size - 1].id = userID.toString()
        }

//        (activity as MainActivity).setProgressIndicator(view, true)

        view.findViewById<TextView>(R.id.user_name_text).text = sharedPref?.getString("user_name", "User Name")!! + "'s Liked Movies";

        // Grid type of RecyclerView
        val viewAdapter = MyAdapter(view, userID, mCards)
        if (mCards.size == 1) // if empty
            viewAdapter.loadMore()

        val recyclerView = view.findViewById<RecyclerView>(R.id.leaderboard_list);

        val gridSpandCount = 2
        val gridManager = GridLayoutManager(activity, gridSpandCount, GridLayoutManager.VERTICAL, false)
        gridManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int) =  when (position) {
                (mCards.size - 1) -> gridSpandCount // the last item: button
                else -> 1
            }
        }

        recyclerView.layoutManager = gridManager
        recyclerView.run {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }

        return view
    }

}

class MyAdapter(private val view : View, private val userID : Int, private val myDataset: MutableList<CardMovie>) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    private val picasso: Picasso = Picasso.get()
    private val loadNum = 4

    class ViewHolder(val item: View) : RecyclerView.ViewHolder(item)

    val TYPE_ITEM = 1
    val TYPE_LOAD = 2

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        // create a new view
        lateinit var itemView : View

        if(viewType == TYPE_ITEM) {
            itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_view_item, parent, false)
        }
        else if(viewType == TYPE_LOAD) {

            itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_view_button, parent, false)
        }


        return ViewHolder(itemView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        holder.item.findViewById<TextView>(R.id.user_name_text).text = myDataset[position]

        if(holder.itemViewType == TYPE_ITEM) {

            val movieTitle = myDataset[position % itemCount]?.title?: "NoTitle"
            holder.item.findViewById<AppCompatTextView>(R.id.user_name_text).text = movieTitle

            // if there is a saved URL, load it
            if(myDataset[position % itemCount]?.imageURL != "") {
                picasso.load(myDataset[position % itemCount]?.imageURL)
                        .error(R.drawable.movie_post_error)
                        .into(holder.itemView.findViewById<ImageView>(R.id.card_movie_image))

                return
            }

            // https://docs.microsoft.com/en-us/bing/search-apis/bing-image-search/reference/query-parameters
            val searchOptions = "&count=1" + "&aspect=Tall" + "&size=Medium"

            // https://docs.microsoft.com/en-us/bing/search-apis/bing-image-search/reference/endpoints
            (holder.itemView.context.getString(R.string.Azure_Search_URL) + "?q=" + movieTitle + searchOptions)
                    .httpGet()
                    .header("Ocp-Apim-Subscription-Key", holder.itemView.context.getString(R.string.Azure_key))
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

                                holder.itemView.findViewById<ImageView>(R.id.user_avatar_image).setBackgroundColor(Color.parseColor("#$accentColor"))

                                myDataset[position % itemCount]?.imageURL = thumbnailUrl // save the searched URL
                                picasso.load(thumbnailUrl)
//                                    .placeholder(R.drawable.movie_post_0) // replaced "movie_post_0" to the given "accentColor"
                                        .error(R.drawable.movie_post_error)
                                        .into(holder.itemView.findViewById<ImageView>(R.id.user_avatar_image))
                            }
                        }
                    }
        }
        else if(holder.itemViewType == TYPE_LOAD) {
            holder.item.findViewById<Button>(R.id.loadLikedMovies_bts).setOnClickListener {

                loadMore()
            }
        }
     }

    public fun loadMore() {

        (view.context as MainActivity).setProgressIndicator(view, true)

        val startRange = myDataset.size - 1
        val loadRange = "&start=" + startRange.toString() + "&end=" + (startRange + loadNum).toString()

        (view.context.getString(R.string.url_main) + "likedMovies?user_id=").plus(userID.toString()).plus(loadRange)
//        (getString(R.string.url_main) + "recoMovies?user_id=").plus(userID.toString())
                .httpGet()
                .also { Log.d(MainActivity::class.java.simpleName, it.cUrlString()) }
                .responseString { _, _, result ->
                    when (result) {
                        is Result.Failure -> {
                            val data = result.error.errorData.toString(Charsets.UTF_8)
                            Log.v(MainActivity::class.java.simpleName, "Failure, ErrorData: $data")

                            (view.context as MainActivity).setProgressIndicator(view, false)

                            val message = JSONObject(data).getString("message")?:"Error"
                            Toast.makeText(view.context, message, Toast.LENGTH_LONG).show()
                        }
                        is Result.Success -> {
                            val data = result.get()
                            println(data)
                            Log.v(MainActivity::class.java.simpleName, "Success: $data")

                            (view.context as MainActivity).setProgressIndicator(view, false)

                            // load and init the card data
                            val jsonArray = JSONArray(data);
//                            mMaxSize = minOf(mMaxSize, jsonArray.length());
                            for (i in 0 until jsonArray.length()) {
                                myDataset.add(myDataset.size - 1,
                                        CardMovie(
                                                i,
                                                jsonArray.getJSONObject(i).getString("movie_id"),
                                                jsonArray.getJSONObject(i).getString("title"),
                                                jsonArray.getJSONObject(i).getString("genres"),
                                                ""
                                        ))
                            }

                            this.notifyDataSetChanged() // update
                        }
                    }
                }

//        for(i in 0 until 4) {
//            myDataset.add(myDataset.size - 1, CardMovie(
//                    i,
//                    "id.",
//                    "title..$i",
//                    "genre..."
//            ))
//        }

//        this.notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return myDataset.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == (myDataset.size - 1)) TYPE_LOAD else TYPE_ITEM
    }
}
