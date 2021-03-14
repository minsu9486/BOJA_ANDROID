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
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.navigationadvancedsample.MainActivity
import com.example.android.navigationadvancedsample.R
import com.example.android.navigationadvancedsample.homescreen.CardMovie
import com.github.kittinunf.fuel.core.extensions.cUrlString
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import org.json.JSONArray


/**
 * Shows a static leaderboard with multiple users.
 */
class Leaderboard : Fragment() {

    val TAG = MainActivity::class.java.simpleName

    private var mMaxSize = 4
    private var userID = 0
    private var mCards = arrayOfNulls<CardMovie>(mMaxSize)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_leaderboard, container, false)

        val sharedPref = activity?.getSharedPreferences("LoginStatus", Context.MODE_PRIVATE)

        userID = sharedPref?.getInt("user_id", 0)!!
        (getString(R.string.url_main) + "recoMovies?user_id=").plus(userID.toString())
                .httpGet()
                .also { Log.d(TAG, it.cUrlString()) }
                .responseString { _, _, result ->
                    when (result) {
                        is Result.Failure -> {
                            val data = result.error
                            Log.v(TAG, "Failure, ErrorData: $data")

                            val message = result.error.message ?: "Error"
                            Toast.makeText(view.context, message, Toast.LENGTH_LONG).show()
                        }
                        is Result.Success -> {
                            val data = result.get()
                            println(data)
                            Log.v(TAG, "Success: $data")

                            val jsonArray = JSONArray(data);
                            // init the card data
                            mMaxSize = jsonArray.length();
                            for (i in 0 until jsonArray.length()) {
                                mCards[i] = CardMovie(
                                        i,
                                        jsonArray.getJSONObject(i).getString("movie_id"),
                                        jsonArray.getJSONObject(i).getString("title"),
                                        jsonArray.getJSONObject(i).getString("genres"),
                                );
                            }
                        }
                    }
                }

        val viewAdapter = MyAdapter(mCards, mMaxSize)
        val recyclerView = view.findViewById<RecyclerView>(R.id.leaderboard_list);

        val gridManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridManager

//        val x = (resources.displayMetrics.density * 4).toInt() //converting dp to pixels
//        recyclerView.addItemDecoration(MarginItemDecoration(x)) //setting space between items in RecyclerView

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

class MyAdapter(private val myDataset: Array<CardMovie?>, private val itemCount: Int) :
    RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class ViewHolder(val item: View) : RecyclerView.ViewHolder(item)


    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): ViewHolder {
        // create a new view
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_view_item, parent, false)


        return ViewHolder(itemView)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
//        holder.item.findViewById<TextView>(R.id.user_name_text).text = myDataset[position]

        holder.item.findViewById<ImageView>(R.id.user_avatar_image)
                .setImageResource(R.drawable.movie_post_0)

        holder.item.findViewById<AppCompatTextView>(R.id.user_name_text).text = myDataset[position % itemCount]?.title
                ?: "title uninit"

//        holder.item.setOnClickListener {
//            val bundle = bundleOf(USERNAME_KEY to myDataset[position])

//            holder.item.findNavController().navigate(
//                    R.id.action_leaderboard_to_userProfile,
//                bundle)
        }

    override fun getItemCount(): Int {
        return itemCount
    }

//    // Return the size of your dataset (invoked by the layout manager)
//    override fun getItemCount() = myDataset.size

//    companion object {
//        const val USERNAME_KEY = "userName"
//    }
}

class MarginItemDecoration(
        private val spaceSize: Int,
        private val spanCount: Int = 1,
        private val orientation: Int = GridLayoutManager.VERTICAL
) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView, state: RecyclerView.State
    ) {
        with(outRect) {
            if (orientation == GridLayoutManager.VERTICAL) {
                if (parent.getChildAdapterPosition(view) < spanCount) {
                    top = spaceSize
                }
                if (parent.getChildAdapterPosition(view) % spanCount == 0) {
                    left = spaceSize
                }
            } else {
                if (parent.getChildAdapterPosition(view) < spanCount) {
                    left = spaceSize
                }
                if (parent.getChildAdapterPosition(view) % spanCount == 0) {
                    top = spaceSize
                }
            }

            right = spaceSize
            bottom = spaceSize
        }
    }
}

//private val listOfAvatars = listOf(
//    R.drawable.movie_post_1,
//    R.drawable.movie_post_2,
//    R.drawable.movie_post_3,
//    R.drawable.movie_post_4,
//    R.drawable.movie_post_5,
//    R.drawable.movie_post_6
//)
