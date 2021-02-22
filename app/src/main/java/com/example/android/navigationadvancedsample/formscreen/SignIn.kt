package com.example.android.navigationadvancedsample.formscreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.navigationadvancedsample.MainActivity
import com.example.android.navigationadvancedsample.R
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.extensions.cUrlString
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result

class SignIn : Fragment() {

    private val TAG = MainActivity::class.java.simpleName

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        view.findViewById<Button>(R.id.signin_btn).setOnClickListener {

//            findNavController().navigate(R.id.action_signIn_to_registered)

            val textUsername = view.findViewById<EditText>(R.id.user_name_text).text;
            val textPassword = view.findViewById<EditText>(R.id.password_text).text;

            (getString(R.string.url_main) + "signin?username=").plus(textUsername.toString()).plus("&passcode=").plus(textPassword.toString())
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
                                findNavController().navigate(R.id.action_signIn_to_registered)
                            }
                        }
                    }

        }

        return view
    }
}