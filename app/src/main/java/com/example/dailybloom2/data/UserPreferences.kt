package com.example.dailybloom2.data

import android.content.Context
import android.content.SharedPreferences

object UserPreferences {
    private const val PREFS_NAME = "user_prefs"
    private const val KEY_FIRST_NAME = "first_name"
    private const val KEY_LAST_NAME = "last_name"
    private const val KEY_EMAIL = "email"
    private const val KEY_PHONE = "phone"
    private const val KEY_PASSWORD = "password"
    private const val KEY_PROFILE_PIC = "profile_pic"
    private const val KEY_REMEMBER_ME = "remember_me"
    private const val KEY_SAVED_EMAIL = "saved_email"
    private const val KEY_SAVED_PASSWORD = "saved_password"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveUser(
        context: Context,
        firstName: String,
        lastName: String,
        email: String,
        phone: String,
        password: String,
        profilePic: String
    ) {
        val prefs = getSharedPreferences(context)
        prefs.edit().apply {
            putString(KEY_FIRST_NAME, firstName)
            putString(KEY_LAST_NAME, lastName)
            putString(KEY_EMAIL, email)
            putString(KEY_PHONE, phone)
            putString(KEY_PASSWORD, password)
            putString(KEY_PROFILE_PIC, profilePic)
            apply()
        }
    }

    fun getUserEmail(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_EMAIL, null)
    }

    fun getUserPassword(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_PASSWORD, null)
    }

    fun getUserFirstName(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_FIRST_NAME, null)
    }

    fun getUserLastName(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_LAST_NAME, null)
    }

    fun getUserPhone(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_PHONE, null)
    }

    fun getUserProfilePic(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_PROFILE_PIC, null)
    }

    fun clearUser(context: Context) {
        val prefs = getSharedPreferences(context)
        prefs.edit().clear().apply()
    }

    // Remember Me functionality
    fun setRememberMe(context: Context, rememberMe: Boolean, email: String = "", password: String = "") {
        val prefs = getSharedPreferences(context)
        prefs.edit().apply {
            putBoolean(KEY_REMEMBER_ME, rememberMe)
            if (rememberMe) {
                putString(KEY_SAVED_EMAIL, email)
                putString(KEY_SAVED_PASSWORD, password)
            } else {
                remove(KEY_SAVED_EMAIL)
                remove(KEY_SAVED_PASSWORD)
            }
            apply()
        }
    }

    fun isRememberMeEnabled(context: Context): Boolean {
        return getSharedPreferences(context).getBoolean(KEY_REMEMBER_ME, false)
    }

    fun getSavedEmail(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_SAVED_EMAIL, null)
    }

    fun getSavedPassword(context: Context): String? {
        return getSharedPreferences(context).getString(KEY_SAVED_PASSWORD, null)
    }
}