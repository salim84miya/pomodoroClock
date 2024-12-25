package com.example.clock.utility

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.graphics.Color

class MyApp: Application() {

  companion object{
      lateinit var sharedPref: SharedPreferences
  }

    override fun onCreate() {
        super.onCreate()

        sharedPref = getSharedPreferences("com.example.clock.salim.Preferences", Context.MODE_PRIVATE)

        if(!sharedPref.contains("focus")){
            with(sharedPref.edit()){
                putInt("focus",600)
                putInt("rest",300)
                putInt("focus_timer_color", Color.argb(250, 112, 0, 0))
                putInt("break_timer_color",Color.argb(255, 40, 186, 251))
                apply()
            }
        }
    }
}