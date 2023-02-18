package com.codenode.budgetlens.data

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.codenode.budgetlens.BuildConfig
import com.codenode.budgetlens.common.BearerToken
import com.codenode.budgetlens.common.GlobalSharedPreferences
import com.codenode.budgetlens.home.HomePageActivity
import com.codenode.budgetlens.login.LoginActivity
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

        fun loadProfileFromAPIs(context: Context) {

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

                                //After loading from API, save it to shared preference for persistence
                                //and update the user profile
                                updateProfile(
                                    false,
                                    username,
                                    firstName,
                                    lastName,
                                    email,
                                    telephoneNumber,
                                    context,
                                    null
                                )

                                Log.i("Successful", "Successfully loaded profile from API.")
                            } else {
                                Log.i(
                                    "Error",
                                    "Something went wrong ${response.message} ${response.headers}"
                                )
                            }

                        } else {
                            Log.e(
                                "Error",
                                "Something went wrong ${response.message} ${response.headers}"
                            )
                        }
                    }
                }
            })
        }
        fun loadProfileFromAPI(context: Context,activity:LoginActivity) {

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

                                //After loading from API, save it to shared preference for persistence
                                //and update the user profile
                                updateProfile(
                                    false,
                                    username,
                                    firstName,
                                    lastName,
                                    email,
                                    telephoneNumber,
                                    context,
                                    null
                                )
                                activity.finish()
                                context.startActivity(
                                    Intent(context,
                                        HomePageActivity::class.java)
                                )

                                Log.i("Successful", "Successfully loaded profile from API.")
                            } else {
                                Log.i(
                                    "Error",
                                    "Something went wrong ${response.message} ${response.headers}"
                                )
                            }

                        } else {
                            Log.e(
                                "Error",
                                "Something went wrong ${response.message} ${response.headers}"
                            )
                        }
                    }
                }
            })
        }

        /**
         * Updates the profile.
         * If a user is editing the profile via the dialog, isUpdatingBackend should be true, false otherwise.
         */
        fun updateProfile(
            isUpdatingBackend: Boolean = false,
            username: String,
            firstName: String,
            lastName: String,
            email: String,
            telephoneNumber: String,
            context: Context,
            dialog: AlertDialog?
        ) {
            if (isUpdatingBackend) {
                //Handles updating profile from the profile edit dialog.
                updateUserProfileAPI(
                    username,
                    firstName,
                    lastName,
                    email,
                    telephoneNumber,
                    context, dialog
                )
            } else {
                //Handles updating profile from a login via fetching from api
                userProfile.username = username
                userProfile.firstName = firstName
                userProfile.lastName = lastName
                userProfile.email = email
                userProfile.telephoneNumber = telephoneNumber
                updateSharedPreferences(context)
            }
        }

        /**
         * Updates shared preferences for user profile.
         */
        private fun updateSharedPreferences(context: Context) {
            //Update the preference
            val preferences: SharedPreferences = GlobalSharedPreferences.get(context)
            val json = Gson().toJson(userProfile)
            preferences.edit().putString("UserProfile", json).apply()
        }

        private fun updateUserProfileAPI(
            username: String,
            firstName: String,
            lastName: String,
            email: String,
            telephoneNumber: String,
            context: Context, dialog: AlertDialog?
        ) {
            val registrationPost = OkHttpClient()
            val body: RequestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("username", username)
                .addFormDataPart("first_name", firstName)
                .addFormDataPart("last_name", lastName)
                .addFormDataPart("email", email)
                .addFormDataPart("telephone_number", telephoneNumber)
                .build()

            val request = Request.Builder()
                .url("http://${BuildConfig.ADDRESS}:${BuildConfig.PORT}/userprofile/")
                .method("PUT", body)
                .addHeader("Authorization", "Bearer ${BearerToken.getToken(context)}")
                .addHeader("Content-Type", "text/plain")
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
                                userProfile.username = username
                                userProfile.firstName = firstName
                                userProfile.lastName = lastName
                                userProfile.email = email
                                userProfile.telephoneNumber = telephoneNumber
                                updateSharedPreferences(context)

                                Handler(Looper.getMainLooper()).post {
                                    Toast.makeText(
                                        context,
                                        "Update successful.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                                dialog?.dismiss()

                                Log.i("Successful", "Successfully update profile from API.")
                            } else {
                                Handler(Looper.getMainLooper()).post {
                                    Toast.makeText(context, "Update failed.", Toast.LENGTH_SHORT)
                                        .show()
                                }
                                Log.i(
                                    "Error",
                                    "Something went wrong ${response.message} ${response.headers}"
                                )
                            }

                        } else {
                            Log.e(
                                "Error",
                                "Something went wrong$ ${response.message} ${response.headers}"
                            )
                        }
                    }
                }
            })


        }

        fun getFullName(): String {
            return userProfile.firstName + " " + userProfile.lastName
        }
    }
}