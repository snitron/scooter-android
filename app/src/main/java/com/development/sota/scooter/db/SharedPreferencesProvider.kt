package com.development.sota.scooter.db

import android.content.Context

open class SharedPreferencesProvider(private val context: Context) {
    open val sharedPreferences = context.getSharedPreferences("account", Context.MODE_PRIVATE)
}