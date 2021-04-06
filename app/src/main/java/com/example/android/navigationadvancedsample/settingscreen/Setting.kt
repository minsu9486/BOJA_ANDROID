package com.example.android.navigationadvancedsample.settingscreen

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.transition.TransitionManager
import android.view.*
import android.widget.Button
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.navigationadvancedsample.MainActivity
import com.example.android.navigationadvancedsample.R
import com.google.android.material.bottomnavigation.BottomNavigationView


class Setting : Fragment()
{
    private val TAG = MainActivity::class.java.simpleName

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_setting, container, false);


        view.findViewById<Button>(R.id.credits_btn).setOnClickListener {

            // Custom view to show in popup window
            val viewCredits = inflater.inflate(R.layout.layout_credits, null)
            val popupWindow = PopupWindow(
                    viewCredits,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )

            // close the credits when a user touches the screen
            viewCredits.findViewById<LinearLayout>(R.id.layout_credits).setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_DOWN) {
                    popupWindow.dismiss()
                }
                true
            }

            //Set a dismiss listener for the credits
            popupWindow.setOnDismissListener {
                // restore the dim effect
                view.findViewById<ConstraintLayout>(R.id.constraintLayout_setting).alpha = 1F
            }

            // dim effect
            view.findViewById<ConstraintLayout>(R.id.constraintLayout_setting).alpha = 0.6F

            // show the credits
            popupWindow.isOutsideTouchable = false
            popupWindow.isFocusable = true // any unexpected action: close the credits
            popupWindow.showAtLocation(container, Gravity.CENTER, 0, 0)
        }

        view.findViewById<Button>(R.id.signoff_btn).setOnClickListener {

            val sharedPref = activity?.getSharedPreferences("LoginStatus", Context.MODE_PRIVATE)
            sharedPref?.edit()?.putBoolean("DidLogIn", false)?.apply()

            // hide the bottom navigation
            (activity as MainActivity).setBottomNavigationVisibility(View.INVISIBLE)

            // move to the login screen
            (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottom_nav).selectedItemId = R.id.navGraph_home
        }

        return view
    }
}