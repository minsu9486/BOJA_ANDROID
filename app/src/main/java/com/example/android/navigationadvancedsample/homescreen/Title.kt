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

package com.example.android.navigationadvancedsample.homescreen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.android.navigationadvancedsample.MainActivity
import com.example.android.navigationadvancedsample.R
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.extensions.cUrlString
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.StackFrom
import org.json.JSONArray
import org.json.JSONObject


/**
 * Shows the main title screen with a button that navigates to [About].
 */
class Title : Fragment(), CardStackListener {

    val TAG = MainActivity::class.java.simpleName

    private lateinit var manager: CardStackLayoutManager
    private lateinit var viewAdapter: CardAdapter
    private lateinit var loadButton: Button

    private val mMaxSize = 4 // 10
    private var userID = 0

    private var mCurrIndex = 0
    private var mCards = arrayOfNulls<CardMovie>(mMaxSize)

    private var mLikedCards = arrayOfNulls<String>(mMaxSize)
    private var mLikedIndex = 0

    private var mHateCards = arrayOfNulls<String>(mMaxSize)
    private var mHateIndex = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_title, container, false)

        // get login data
        val sharedPref = activity?.getSharedPreferences("LoginStatus", Context.MODE_PRIVATE)
        val didLogIn = sharedPref?.getBoolean("DidLogIn", false)

        // no login data
        if(didLogIn == false) {

            // hide the bottom navigation
            (activity as MainActivity).setBottomNavigationVisibility(View.INVISIBLE)

            findNavController().navigate(R.id.action_title_to_register)
        }
        // has login data
        else {

            (activity as MainActivity).setProgressIndicator(view, true)

            loadButton = view.findViewById<Button>(R.id.loadRecoMovies_bts)

            // reset
            this.resetData(view)

            view.findViewById<Button>(R.id.loadRecoMovies_bts).setOnClickListener {
                Toast.makeText(view.context, loadButton.contentDescription, Toast.LENGTH_LONG).show()
            }

            // show the bottom navigation
            (activity as MainActivity).setBottomNavigationVisibility(View.VISIBLE)

            userID = sharedPref?.getInt("user_id", 0)!!
            (getString(R.string.url_main) + "starts?user_id=").plus(userID.toString())
                    .httpGet()
                    .also { Log.d(TAG, it.cUrlString()) }
                    .responseString { _, _, result ->
                        when (result) {
                            is Result.Failure -> {
                                val data = result.error.errorData.toString(Charsets.UTF_8)
                                Log.v(TAG, "Failure, ErrorData: $data")

                                (activity as MainActivity).setProgressIndicator(view, false)

                                var message = if(data != "")
                                    JSONObject(data).getString("message")?:"Error"
                                else
                                    "Error"

                                Toast.makeText(view.context, message, Toast.LENGTH_LONG).show()
                            }
                            is Result.Success -> {
                                val data = result.get()
                                Log.v(TAG, "Success: $data")

                                (activity as MainActivity).setProgressIndicator(view, false)

                                // buttons
                                view.findViewById<Button>(R.id.loadRecoMoviesDummy_bts).visibility = View.VISIBLE
                                view.findViewById<ImageView>(R.id.deco_thumbDown_bts).visibility = View.VISIBLE
                                view.findViewById<ImageView>(R.id.deco_thumbUp_bts).visibility = View.VISIBLE

                                val jsonArray = JSONArray(data);
                                // init the card data (until mMaxSize or jsonArray.length())
                                for (i in 0 until mMaxSize) {
                                    mCards[i] = CardMovie(
                                            i,
                                            jsonArray.getJSONObject(i).getString("movieId"),
                                            jsonArray.getJSONObject(i).getString("title"),
                                            jsonArray.getJSONObject(i).getString("genres"),
                                    );
                                }

                                if(mCurrIndex == mMaxSize)
                                    mCurrIndex = 0;

                                manager = CardStackLayoutManager(this.context, this)
                                viewAdapter = CardAdapter(mCards, mMaxSize)

                                view.findViewById<RecyclerView>(R.id.cardstack_list).run {
                                    // use this setting to improve performance if you know that changes
                                    // in content do not change the layout size of the RecyclerView
                                    setHasFixedSize(true)

                                    manager.setDirections(arrayListOf(Direction.Right, Direction.Left))
                                    manager.setCanScrollHorizontal(true)
                                    manager.setVisibleCount(5)
                                    manager.setStackFrom(StackFrom.Bottom)
                                    manager.setTranslationInterval(6.0f)
                                    manager.setScaleInterval(0.95f)
                                    manager.setMaxDegree(20.0f)
                                    manager.topPosition = mCurrIndex;

                                    layoutManager = manager
                                    adapter = viewAdapter // specify an viewAdapter (see also next example)
                                }

                            }
                        }
                    }
        }

//        if(mCurrIndex == mMaxSize)
//            mCurrIndex = 0;
//
//        // init the card data
//        for (i in listOfTitles.indices) {
//            mCards[i] = CardMovie(i, listOfTitles[i]);
//        }

//        view.findViewById<Button>(R.id.about_btn).setOnClickListener {
//            findNavController().navigate(R.id.action_title_to_about)
//        }

//        // when pressed back button
//        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
//            // Handle the back button event
//            exitProcess(0)
//        }

        return view
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onCardDragging(direction: Direction, ratio: Float) {
    }

    override fun onCardSwiped(direction: Direction) {

        // the swiped movie = mCards[manager.topPosition - 1]

        if(direction == Direction.Left) {
            mHateCards[mHateIndex] = mCards[manager.topPosition - 1]?.id

            ++mHateIndex
        }
        else if(direction == Direction.Right) {
            mLikedCards[mLikedIndex] = mCards[manager.topPosition - 1]?.id

            ++mLikedIndex
        }

        // last position reached, do something
        if (manager.topPosition == viewAdapter.itemCount) {

            loadButton.visibility = View.VISIBLE

            // Liked Movie IDs
            var movieLikedIDs = ""
                for(i in 0 until mLikedIndex)
                    movieLikedIDs += "&movie_id_like=" + mLikedCards[i].toString()

            // Hate Movie IDs
            var movieHateIDs = ""
            for(i in 0 until mHateIndex)
                movieHateIDs += "&movie_id_hate=" + mHateCards[i].toString()

            // HTTP REQUEST POST
            (getString(R.string.url_main) + "ratings?user_id=").plus(userID.toString()).plus(movieLikedIDs).plus(movieHateIDs)
                .httpPost()
                .authentication()
                .also { Log.d(TAG, it.cUrlString()) }
                .responseString { _, _, result ->
                    when (result) {
                        is Result.Failure -> {
                            val data = result.error.errorData.toString(Charsets.UTF_8)
                            Log.v(TAG, "Failure, ErrorData: $data")

                            var message = if(data != "")
                                JSONObject(data).getString("message")?:"Error"
                            else
                                "Error"
                        }
                        is Result.Success -> {
                            val data = result.get()
                            println(data)
                            Log.v(TAG, "Success: $data")

                        }
                    }
                }
        }

        // save the current position
        mCurrIndex = manager.topPosition;
    }

    override fun onCardRewound() {
    }

    override fun onCardCanceled() {
    }

    override fun onCardAppeared(view: View, position: Int) {
    }

    override fun onCardDisappeared(view: View, position: Int) {
    }

    private fun resetData(view : View) {
        mCurrIndex = 0
        mCards = arrayOfNulls<CardMovie>(mMaxSize)
        mLikedCards = arrayOfNulls<String>(mMaxSize)
        mLikedIndex = 0
        mHateCards = arrayOfNulls<String>(mMaxSize)
        mHateIndex = 0

        view.findViewById<Button>(R.id.loadRecoMovies_bts).visibility = View.INVISIBLE
        view.findViewById<Button>(R.id.loadRecoMoviesDummy_bts).visibility = View.INVISIBLE
        view.findViewById<ImageView>(R.id.deco_thumbDown_bts).visibility = View.INVISIBLE
        view.findViewById<ImageView>(R.id.deco_thumbUp_bts).visibility = View.INVISIBLE
    }

    private fun loadData(view : View) {

    }
}

//private val listOfMovies = listOf(
//        R.drawable.movie_post_1,
//        R.drawable.movie_post_2,
//        R.drawable.movie_post_3,
//        R.drawable.movie_post_4,
//        R.drawable.movie_post_5,
//        R.drawable.movie_post_6
//)
//
//private var listOfTitles = arrayOf(
//        "Moonlight",
//        "Joker",
//        "Kong: Skull Island",
//        "The Nightingale",
//        "Spider-Man: Into the Spider-Verse",
//        "The Assassin"
//)