package com.codenode.budgetlens.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.common.BearerToken
import com.codenode.budgetlens.common.GlobalSharedPreferences
import com.google.gson.Gson
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


class UserProfile {

    companion object {
        val userProfile = Profile()

        fun loadProfileFromGlobalPrefs(context: Context, gson: Gson) {

            val json = GlobalSharedPreferences.get(context).getString("UserProfile", "")

            val profile = gson.fromJson(json, Profile::class.java)

            //Null when no profile ever loaded before
            if (profile != null) {
                userProfile.isLoaded = true
                userProfile.username = profile.username
                userProfile.firstName = profile.firstName
                userProfile.lastName = profile.lastName
                userProfile.email = profile.email
                userProfile.telephoneNumber = profile.telephoneNumber
                userProfile.dateOfBirth = profile.dateOfBirth
            }
        }

        fun loadProfileFromAPI(context: Context) {

            val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/userprofile/"

            val registrationPost = OkHttpClient()
            val request = Request.Builder()
                .url(url)
                .method("GET", null)
                .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
                .addHeader("Content-Type", "application/json")
                .build()

            registrationPost.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                }

                override fun onResponse(call: Call, response: Response) {
                    Log.i("Response", "Got the response from server")
                    response.use {
                        if (response.isSuccessful) {
                            val responseBody = response.body?.string()
                            if (responseBody != null) {
                                val jsonObject = JSONObject(responseBody.toString())
                                //Parse the bearer token from response
                                val username = jsonObject.getString("username")
                                val firstName = jsonObject.getString("first_name")
                                val lastName = jsonObject.getString("last_name")
                                val email = jsonObject.getString("email")
                                val telephoneNumber = jsonObject.getString("telephone_number")
                                val dateOfBirth = jsonObject.getString("dateOfBirth")

                                //After loading from API, save it to shared preference for persistence
                                //and update the user profile
                                updateProfile(
                                    false,
                                    username,
                                    firstName,
                                    lastName,
                                    email,
                                    telephoneNumber,
                                    dateOfBirth,
                                    context
                                )

                                Log.i("Successful", "Successfully loaded profile from API.")
                            } else {
                                Log.i("Error", "Something went wrong${response.body?.string()}")
                            }

                        } else {
                            println("failed to load profile from API.")
                            Log.e(
                                "Error",
                                "Something went wrong${response.body?.string()} ${response.message} ${response.headers}"
                            )
                        }
                    }
                }
            })
        }

        /**
         * Updates the profile.
         * If a user registers updateBackend should be false.
         * If a user modifies it themselves through the app,
         * updateBackend should be true to update the backend.
         */
        fun updateProfile(
            updateBackend: Boolean = false,
            username: String,
            firstName: String,
            lastName: String,
            email: String,
            telephoneNumber: String,
            dateOfBirth:String,
            context: Context
        ) {

            userProfile.username = username
            userProfile.firstName = firstName
            userProfile.lastName = lastName
            userProfile.email = email
            userProfile.telephoneNumber = telephoneNumber
            userProfile.dateOfBirth=dateOfBirth

            //Update the preference
            val preferences: SharedPreferences = GlobalSharedPreferences.get(context)
            val json = Gson().toJson(userProfile);
            preferences.edit().putString("UserProfile", json).apply()

            //We only need to update the backend if the user is modifying it themselves
            if (updateBackend) {
                //TODO send the http request to update backend
                val url = "http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/userprofile?firstName="+firstName+
                        "&lastName="+lastName+"&email="+email+"&telephoneNumber="+telephoneNumber+"&dateOfBirth="+dateOfBirth

                val registrationPost = OkHttpClient()
                val request = Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
                    .addHeader("Content-Type", "application/json")
                    .build()

                registrationPost.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        Log.i("Response", "Got the response from server")
                        response.use {
                            if (response.isSuccessful) {
                                val responseBody = response.body?.string()
                                if (responseBody != null) {
                                    val jsonObject = JSONObject(responseBody.toString())
                                    //After loading from API, save it to shared preference for persistence
                                    //and update the user profile
                                    updateProfile(
                                        false,
                                        username,
                                        firstName,
                                        lastName,
                                        email,
                                        telephoneNumber,
                                        dateOfBirth,
                                        context
                                    )
                                    Log.i("Successful", "Successfully update profile from API.")
                                } else {
                                    Log.i("Error", "Something went wrong${response.body?.string()}")
                                }

                            } else {
                                println("failed to load profile from API.")
                                Log.e(
                                    "Error",
                                    "Something went wrong${response.body?.string()} ${response.message} ${response.headers}"
                                )
                            }
                        }
                    }
                })

            }
        }
    }
}