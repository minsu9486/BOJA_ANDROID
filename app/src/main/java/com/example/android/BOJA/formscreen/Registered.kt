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

package com.example.android.BOJA.formscreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.BOJA.MainActivity
import com.example.android.BOJA.R


/**
 * Shows "Done".
 */
class Registered : Fragment() {
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_registered, container, false);

        // show the bottom navigation
        (activity as MainActivity).setBottomNavigationVisibility(View.VISIBLE)

        // touch motion
        view.findViewById<ConstraintLayout>(R.id.title_constraint).setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                findNavController().navigate(R.id.action_register_to_title)
            }
            true
        }

        return view
    }
}
