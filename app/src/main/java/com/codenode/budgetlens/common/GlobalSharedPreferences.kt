package com.codenode.budgetlens.common

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.codenode.budgetlens.R
import com.codenode.budgetlens.data.Profile
import com.codenode.budgetlens.data.UserProfile
import com.google.gson.Gson


/**
 * Used to store global preferences used in all activities.
 */
class GlobalSharedPreferences private constructor() {

    companion object {
        private val gson = Gson()

        fun get(context: Context): SharedPreferences {
            return context.getSharedPreferences(
                context.getString(R.string.global_preferences), MODE_PRIVATE
            )
        }

        /**
         * Load global preferences.
         */
        fun load(context: Context) {

            UserProfile.loadProfileFromGlobalPrefs(context, gson)

        }
    }
}