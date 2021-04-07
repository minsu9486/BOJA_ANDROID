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
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
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
    private var isFirstAttempt = false

    private var mCurrIndex = 0
    private var mCards = arrayOfNulls<CardMovie>(mMaxSize)
    private var isRecoType = false

    private var mLikedCards = arrayOfNulls<String>(mMaxSize)
    private var mLikedIndex = 0

    private var mHateCards = arrayOfNulls<String>(mMaxSize)
    private var mHateIndex = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_title, container, false)
        loadButton = view.findViewById<Button>(R.id.loadRecoMovies_bts)

        // get login data
        val sharedPref = activity?.getSharedPreferences("LoginStatus", Context.MODE_PRIVATE)
        val didLogIn = sharedPref?.getBoolean("DidLogIn", false)

        // no login data
        if(didLogIn == false) {

            isFirstAttempt = true

            // hide the bottom navigation
            (activity as MainActivity).setBottomNavigationVisibility(View.INVISIBLE)

            findNavController().navigate(R.id.action_title_to_register)
        }
        // has login data
        else {
            if (sharedPref == null) {
                Toast.makeText(view.context, "Critical Error! NO SharedPref!", Toast.LENGTH_LONG).show()
                return view
            }

            // show the bottom navigation
            (activity as MainActivity).setBottomNavigationVisibility(View.VISIBLE)

            // button for "load more"
            view.findViewById<Button>(R.id.loadRecoMovies_bts).setOnClickListener {
                resetData()
                initButtonState(view)
                loadNewCards(inflater, view, sharedPref)
            }

            initButtonState(view)

            // if it is no data...
            if (mCards[0] == null) {
                loadNewCards(inflater, view, sharedPref) // only load a set of data when there is no data
            }
            // if there is a prev data...
            else {
                activateButtonState(view)
                setCardLayout(view)
            }
        }

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

                            var message = if (data != "")
                                JSONObject(data).getString("message") ?: "Error"
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

    private fun resetData() {
        mCurrIndex = 0
        mCards = arrayOfNulls<CardMovie>(mMaxSize)
        mLikedCards = arrayOfNulls<String>(mMaxSize)
        mLikedIndex = 0
        mHateCards = arrayOfNulls<String>(mMaxSize)
        mHateIndex = 0
    }

    private fun initButtonState(view: View) {
        view.findViewById<Button>(R.id.loadRecoMovies_bts).visibility = View.INVISIBLE
        view.findViewById<Button>(R.id.loadRecoMoviesDummy_bts).visibility = View.INVISIBLE
        view.findViewById<ImageView>(R.id.deco_thumbDown_bts).visibility = View.INVISIBLE
        view.findViewById<ImageView>(R.id.deco_thumbUp_bts).visibility = View.INVISIBLE
    }

    private fun activateButtonState(view: View) {
        view.findViewById<Button>(R.id.loadRecoMoviesDummy_bts).visibility = View.VISIBLE
        view.findViewById<ImageView>(R.id.deco_thumbDown_bts).visibility = View.VISIBLE
        view.findViewById<ImageView>(R.id.deco_thumbUp_bts).visibility = View.VISIBLE

        if(isRecoType)
            view.findViewById<TextView>(R.id.data_type).text = "Ⓡ"
        else
            view.findViewById<TextView>(R.id.data_type).text = "Ⓒ"
    }

    private fun createTutorialWindow(inflater: LayoutInflater, view: View) {

        // Custom view to show in popup window
        val viewTutorial = inflater.inflate(R.layout.layout_title_tutorial, null)
        val popupWindow = PopupWindow(
                viewTutorial,
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
        )

        // close the credits when a user touches the screen
        viewTutorial.findViewById<RelativeLayout>(R.id.layout_title_tutorial).setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                popupWindow.dismiss()
            }
            true
        }

        //Set a dismiss listener for the credits
        popupWindow.setOnDismissListener {

            viewTutorial.findViewById<ImageView>(R.id.tuto_thumbUp_bts).clearAnimation()
            viewTutorial.findViewById<ImageView>(R.id.tuto_thumbDown_bts).clearAnimation()

            // restore the dim effect
            view.findViewById<RelativeLayout>(R.id.layout_title).alpha = 1F
        }

        // dim effect
        view.findViewById<RelativeLayout>(R.id.layout_title).alpha = 0.6F

        // show the tutorial
        popupWindow.isOutsideTouchable = false
        popupWindow.isFocusable = true // any unexpected action: close the credits
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

        tutorialAnimation(viewTutorial)
    }

    private fun tutorialAnimation(viewTutorial: View) {

        val thumbUp = viewTutorial.findViewById<ImageView>(R.id.tuto_thumbUp_bts)
        val thumbDown = viewTutorial.findViewById<ImageView>(R.id.tuto_thumbDown_bts)

        thumbUp.animate()
                .translationXBy(200f)
                .translationYBy(-200f)
                .duration = 1000

        thumbUp.animate()
                .scaleXBy(1.5f)
                .scaleYBy(1.5f)
                .duration = 1000
    }

    private fun loadNewCards(inflater: LayoutInflater, view: View, sharedPref: SharedPreferences) {
        (activity as MainActivity).setProgressIndicator(view, true)

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

                            var message = if (data != "")
                                JSONObject(data).getString("message") ?: "Error"
                            else
                                "Error"

                            Toast.makeText(view.context, message, Toast.LENGTH_LONG).show()
                        }
                        is Result.Success -> {
                            val data = result.get()
                            Log.v(TAG, "Success: $data")

                            (activity as MainActivity).setProgressIndicator(view, false)

                            // run tutorial for the new user
                            isFirstAttempt = true
                            if(isFirstAttempt) {
                                createTutorialWindow(inflater, view)
                                isFirstAttempt = false
                            }

                            activateButtonState(view)

                            val jsonBody = JSONObject(data)
                            isRecoType = jsonBody.getBoolean("isReco")
                            if (isRecoType)
                                view.findViewById<TextView>(R.id.data_type).text = "Ⓡ"
                            else
                                view.findViewById<TextView>(R.id.data_type).text = "Ⓒ"

                            // a set of movies
                            val jsonArray = jsonBody.getJSONArray("data")
                            // init the card data (until mMaxSize or jsonArray.length())
                            for (i in 0 until jsonArray.length()) {
                                val item = jsonArray.getJSONObject(i)
                                mCards[i] = CardMovie(
                                        i,
                                        item.getString("movie_id"),
                                        item.getString("title"),
                                        item.getString("genres"),
                                        ""
                                );
                            }

                            if (mCurrIndex == mMaxSize)
                                mCurrIndex = 0;
                        }
                    }

                    setCardLayout(view)
                }
    }

    private fun setCardLayout(view: View) {
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

            if (manager.topPosition == viewAdapter.itemCount)
                loadButton.visibility = View.VISIBLE
        }
    }
}