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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.android.navigationadvancedsample.R
import com.yuyakaido.android.cardstackview.CardStackLayoutManager
import com.yuyakaido.android.cardstackview.CardStackListener
import com.yuyakaido.android.cardstackview.Direction
import com.yuyakaido.android.cardstackview.StackFrom
import kotlin.system.exitProcess


/**
 * Shows the main title screen with a button that navigates to [About].
 */
class Title : Fragment(), CardStackListener {

    private lateinit var manager: CardStackLayoutManager
    private lateinit var viewAdapter: CardAdapter

    private val mMaxSize = 6
    private var mCurrIndex = 0
    private var mCards = arrayOfNulls<CardMovie>(mMaxSize)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_title, container, false)

        // get login data
        val sharedPref = activity?.getSharedPreferences("LoginStatus", Context.MODE_PRIVATE)
        val didLogIn = sharedPref?.getBoolean("DidLogIn", false)

        // no login data
        if(didLogIn == false) {
            findNavController().navigate(R.id.action_title_to_register)
        }
        // has login data
        else {

            for (i in listOfTitles.indices) {
                mCards[i] = CardMovie(i, listOfTitles[i]);
            }

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
//        // last position reached, do something
//        if (manager.topPosition == viewAdapter.itemCount) {
//
//        }

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
                .setImageResource(listOfMovies[position % listOfMovies.size])

        viewHolder.view.findViewById<AppCompatTextView>(R.id.card_movie_title).text = myDataset[position % itemCount]?.title
                ?: "title uninit"
    }

    override fun getItemCount(): Int {
        return itemCount
    }
}

private val listOfMovies = listOf(
        R.drawable.movie_post_1,
        R.drawable.movie_post_2,
        R.drawable.movie_post_3,
        R.drawable.movie_post_4,
        R.drawable.movie_post_5,
        R.drawable.movie_post_6
)

private var listOfTitles = arrayOf(
        "Moonlight",
        "Joker",
        "Kong: Skull Island",
        "The Nightingale",
        "Spider-Man: Into the Spider-Verse",
        "The Assassin"
)