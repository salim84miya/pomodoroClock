package com.example.clock.utility

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class MyApp: Application() {

  companion object{
      lateinit var sharedPref: SharedPreferences
  }

    override fun onCreate() {
        super.onCreate()

        sharedPref = getSharedPreferences("com.example.clock.Preferences", Context.MODE_PRIVATE)

        if(!sharedPref.contains("focus")){
            with(sharedPref.edit()){
                putInt("focus",10)
                putInt("rest",5)
                apply()
            }
        }
    }
}