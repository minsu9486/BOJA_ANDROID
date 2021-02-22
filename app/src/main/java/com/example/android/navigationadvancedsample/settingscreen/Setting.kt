package com.example.android.navigationadvancedsample.settingscreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.fragment.findNavController
import com.example.android.navigationadvancedsample.MainActivity
import com.example.android.navigationadvancedsample.R
import com.example.android.navigationadvancedsample.formscreen.SignIn
import com.google.android.material.bottomnavigation.BottomNavigationView


class Setting : Fragment()
{
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_setting, container, false);


//        view.findViewById<Button>(R.id.signoff_btn).setOnClickListener {
//            findNavController().navigate(R.id.deepLink_setting_to_home)
//        }

        return view
    }
}