package com.codenode.budgetlens.common

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class BearerToken {

    companion object {
        /**
         * Saves the bearer token using EncryptedSharedPreferences.
         */
        fun saveToken(bearerToken: String, context: Context) {
            val pref = getEncryptedSharedPreferences(context)

            with(pref.edit()) {
                putString("Bearer", bearerToken)
                apply()
            }
        }

        /**
         * Returns the bearer token of the logged in user.
         */
        fun getToken(context: Context): String {
            val pref = getEncryptedSharedPreferences(context)

            val text = pref.getString("Bearer", "")

            return text.toString();
        }

        /**
         * Checks if the bearer token exists or not. Returns true if ir does and false otherwise.
         */
        fun exists(context: Context): Boolean {
            val pref = getEncryptedSharedPreferences(context)
            return pref.contains("Bearer")
        }

        /**
         * Deletes the bearer token.
         */
        fun delete(context: Context) {
            val pref = getEncryptedSharedPreferences(context)
            pref.edit().remove("Bearer").apply()
        }

        /**
         * Returns the shared preference object that's encrypted.
         */
        private fun getEncryptedSharedPreferences(context: Context): SharedPreferences {
            val masterKeyAlias = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            return EncryptedSharedPreferences.create(
                context,
                "secret_shared_prefs",
                masterKeyAlias,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }
}