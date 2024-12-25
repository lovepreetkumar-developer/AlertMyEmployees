package com.alert.me.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.alert.me.utils.Constants

class PreferenceProvider(private val context: Context) {

    private val appContext = context.applicationContext

    fun getContext(): Context {
        return context
    }

    private val preferences: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    fun clear(): Boolean {
        return preferences.edit().clear().commit()
    }

    fun setString(key: String, value: String): Boolean {
        return preferences.edit().putString(
            key,
            value
        ).commit()
    }

    fun getString(key: String): String {
        return preferences.getString(key, "")!!
    }

    fun setUserLoggedIn(value: Boolean): Boolean {
        return preferences.edit().putBoolean(
            Constants.KEY_USER_LOGGED_IN,
            value
        ).commit()
    }

    fun isUserLoggedIn(): Boolean {
        return preferences.getBoolean(Constants.KEY_USER_LOGGED_IN, false)
    }

    fun setAdminLoggedIn(value: Boolean): Boolean {
        return preferences.edit().putBoolean(
            Constants.KEY_ADMIN_LOGGED_IN,
            value
        ).commit()
    }

    fun isAdminLoggedIn(): Boolean {
        return preferences.getBoolean(Constants.KEY_ADMIN_LOGGED_IN, false)
    }
}