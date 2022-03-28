package com.development.sota.scooter

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import com.development.sota.scooter.ui.login.presentation.LoginActivity
import com.development.sota.scooter.ui.map.presentation.MapActivity
import com.development.sota.scooter.ui.tutorial.presentation.TutorialActivity
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import moxy.MvpAppCompatActivity

class MainActivity : MvpAppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AppCenter.start(
            application, "APP_ID",
            Analytics::class.java, Crashes::class.java
        )

        sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE)

        if (!sharedPreferences.getBoolean("firstInit", false)) {
            startActivity(Intent(this, LoginActivity::class.java))
        } else if (!sharedPreferences.getBoolean("wasTutorial", false)) {
            startActivity(Intent(this, TutorialActivity::class.java))
        } else {
            startActivity(Intent(this, MapActivity::class.java))
        }
    }
}
