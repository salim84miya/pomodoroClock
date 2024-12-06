package com.example.clock

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.clock.adapter.ClockStateAdapter


class MainActivity : AppCompatActivity() {

    private lateinit var fragmentStateAdapter : ClockStateAdapter
    private lateinit var viewpager2:ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        viewpager2 = findViewById(R.id.viewpager2)
        fragmentStateAdapter = ClockStateAdapter(this)
        viewpager2.adapter = fragmentStateAdapter

    }

}