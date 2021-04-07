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

package com.example.android.navigationadvancedsample

import android.opengl.Visibility
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.progressindicator.CircularProgressIndicator
import android.view.ViewGroup.LayoutParams
import android.view.WindowManager

/**
 * An activity that inflates a layout that has a [BottomNavigationView].
 */
class MainActivity : AppCompatActivity() {

    var currentNavController: LiveData<NavController>? = null
    var isFirstAttempt = false
    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var progressIndicator: CircularProgressIndicator

    override fun onCreate(savedInstanceState: Bundle?) {
        // Make sure this is before calling super.onCreate
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            setupBottomNavigationBar()
            setupProgressIndicator()
        } // Else, need to wait for onRestoreInstanceState
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        if (savedInstanceState != null) {
            super.onRestoreInstanceState(savedInstanceState)
        }
        // Now that BottomNavigationBar has restored its instance state
        // and its selectedItemId, we can proceed with setting up the
        // BottomNavigationBar with Navigation
        setupBottomNavigationBar()
        setupProgressIndicator()
    }

    /**
     * Called on first creation and when restoring state.
     */
    private fun setupBottomNavigationBar() {
        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav)

        val navGraphIds = listOf(R.navigation.home, R.navigation.list, R.navigation.setting)

        // Setup the bottom navigation view with a list of navigation graphs
        val controller = bottomNavigationView.setupWithNavController(
            navGraphIds = navGraphIds,
            fragmentManager = supportFragmentManager,
            containerId = R.id.nav_host_container,
            intent = intent
        )

//        // Whenever the selected controller changes, setup the action bar.
//        controller.observe(this, Observer { navController ->
//            setupActionBarWithNavController(navController)
//        })

        currentNavController = controller
    }

    private fun setupProgressIndicator() {
        progressIndicator = findViewById<CircularProgressIndicator>(R.id.progress_indicator)
        progressIndicator.visibility = View.INVISIBLE
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    fun setBottomNavigationVisibility(visibility: Int) {
        // get the reference of the bottomNavigationView and set the visibility.
        bottomNavigationView.visibility = visibility
    }

    fun setProgressIndicator(view : View, turnOn : Boolean) {
        // TRUE
        if(turnOn) {
            progressIndicator.visibility = View.VISIBLE
            window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        }
        // FALSE
        else {
            progressIndicator.visibility = View.INVISIBLE
            window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        }
    }
}
