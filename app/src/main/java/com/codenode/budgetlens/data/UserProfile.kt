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
                                userProfile.isLoaded = true;
                                userProfile.username = username
                                userProfile.firstName = firstName
                                userProfile.lastName = lastName
                                userProfile.email = email
                                userProfile.telephoneNumber = telephoneNumber

                                //After loading from API, save it to shared preference for persistence

                                val preferences: SharedPreferences =
                                    GlobalSharedPreferences.get(context)
                                val json = Gson().toJson(userProfile);
                                preferences.edit().putString("UserProfile", json).apply()
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

        fun updateProfile(
            username: String,
            firstName: String,
            lastName: String,
            email: String,
            telephoneNumber: String, context: Context
        ) {

            userProfile.username = username
            userProfile.firstName = firstName
            userProfile.lastName = lastName
            userProfile.email = email
            userProfile.telephoneNumber = telephoneNumber

            //Update the preference
            val preferences: SharedPreferences = GlobalSharedPreferences.get(context)
            val json = Gson().toJson(userProfile);
            preferences.edit().putString("UserProfile", json).apply()

            //TODO send the http request to update backend


        }
    }
}