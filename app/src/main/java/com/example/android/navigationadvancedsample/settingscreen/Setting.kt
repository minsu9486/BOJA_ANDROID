package com.example.android.navigationadvancedsample.settingscreen

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.NavHostFragment
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


        view.findViewById<Button>(R.id.signoff_btn).setOnClickListener {

            val sharedPref = activity?.getSharedPreferences("LoginStatus", Context.MODE_PRIVATE)
            sharedPref?.edit()?.putBoolean("DidLogIn", false)?.apply()

            // hide the bottom navigation
            (activity as MainActivity).setBottomNavigationVisibility(View.INVISIBLE)

            // move to the login screen
            (activity as MainActivity).findViewById<BottomNavigationView>(R.id.bottom_nav).selectedItemId = R.id.navGraph_home
        }

//        val bodyJson = """
//            { "instances" : ["42"]
//            }
//         """
//
//        ("http://10.0.2.2:8501/v1/models/reco_movie_lens:predict")
//                .httpPost()
//                .body(bodyJson)
//                .also { Log.d(TAG, it.cUrlString()) }
//                .responseString { _, _, result ->
//                    when (result) {
//                        is Result.Failure -> {
//                            val ex = result.getException()
//                            println(ex)
//                            Log.v(TAG, "Failure: $ex")
//                        }
//                        is Result.Success -> {
//                            val data = result.get()
//                            println(data)
//                            Log.v(TAG, "Success: $data")
//                        }
//                    }
//                }

        return view
    }
}