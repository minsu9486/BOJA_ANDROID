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

package com.example.android.navigationadvancedsample.formscreen

import android.os.Bundle
import android.provider.SyncStateContract.Helpers.update
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.navigationadvancedsample.MainActivity
import com.example.android.navigationadvancedsample.R
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.extensions.cUrlString
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result;

/**
 * Shows a register form to showcase UI state persistence. It has a button that goes to [Registered]
 */
class Register : Fragment() {

    private val TAG = MainActivity::class.java.simpleName

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)

        view.findViewById<Button>(R.id.signup_btn).setOnClickListener {

            val textUsername = view.findViewById<EditText>(R.id.user_name_text).text;
            val textPassword = view.findViewById<EditText>(R.id.password_text).text;
            val textConfirmPassword = view.findViewById<EditText>(R.id.password_confirm_text).text;

            (getString(R.string.url_main) + "users?username=").plus(textUsername.toString()).plus("&passcode=").plus(textPassword.toString())
                    .httpPost()
                    .authentication()
                    .also { Log.d(TAG, it.cUrlString()) }
                    .responseString { _, _, result ->
                        when (result) {
                            is Result.Failure -> {
                                val ex = result.getException()
                                println(ex)
                                Log.v(TAG, "Failure: $ex")
                            }
                            is Result.Success -> {
                                val data = result.get()
                                println(data)
                                Log.v(TAG, "Success: $data")
                                findNavController().navigate(R.id.action_register_to_registered)
                            }
                        }
                    }

        }

        view.findViewById<TextView>(R.id.signin_text).setOnClickListener {
            findNavController().navigate(R.id.action_register_to_sign_in)
        }

        return view
    }
}
