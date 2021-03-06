package com.example.android.BOJA.formscreen

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.android.BOJA.MainActivity
import com.example.android.BOJA.R
import com.github.kittinunf.fuel.core.extensions.authentication
import com.github.kittinunf.fuel.core.extensions.cUrlString
import com.github.kittinunf.fuel.httpPost
import com.github.kittinunf.result.Result
import org.json.JSONObject

class SignIn : Fragment() {

    private val TAG = MainActivity::class.java.simpleName

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_sign_in, container, false);

        view.findViewById<Button>(R.id.signin_btn).setOnClickListener {

//            findNavController().navigate(R.id.action_signIn_to_registered)

            (activity as MainActivity).setProgressIndicator(view, true)

            val textUsername = view.findViewById<EditText>(R.id.user_name_text).text;
            val textPassword = view.findViewById<EditText>(R.id.password_text).text;

            (getString(R.string.url_main) + "signin?username=").plus(textUsername.toString()).plus("&passcode=").plus(textPassword.toString())
                    .httpPost()
                    .authentication()
                    .also { Log.d(TAG, it.cUrlString()) }
                    .responseString { _, _, result ->
                        when (result) {
                            is Result.Failure -> {
                                val data = result.error.errorData.toString(Charsets.UTF_8)
                                Log.v(TAG, "Failure, ErrorData: $data")

                                (activity as MainActivity).setProgressIndicator(view, false)

                                val message = if (data.isEmpty()) "Error" else JSONObject(data).getString("message")

                                Toast.makeText(view.context, message, Toast.LENGTH_LONG).show()
                            }
                            is Result.Success -> {
                                val data = result.get()
                                println(data)
                                Log.v(TAG, "Success: $data")

                                (activity as MainActivity).setProgressIndicator(view, false)

                                val userID = JSONObject(data).getInt("user_id");

                                val sharedPref = activity?.getSharedPreferences("LoginStatus", Context.MODE_PRIVATE)
                                sharedPref?.edit()?.putInt("user_id", userID)?.apply()
                                sharedPref?.edit()?.putBoolean("DidLogIn", true)?.apply()
                                sharedPref?.edit()?.putString("user_name", textUsername.toString())?.apply()

                                findNavController().navigate(R.id.action_signIn_to_registered)
                            }
                        }
                    }

        }

        return view
    }
}