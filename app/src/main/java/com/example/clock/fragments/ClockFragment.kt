package com.example.clock.fragments

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.clock.R
import com.example.clock.databinding.ClockFragmentBinding
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar

class ClockFragment: Fragment(R.layout.clock_fragment) {

    private lateinit var binding: ClockFragmentBinding
    private lateinit var coroutineScope: CoroutineScope
    private lateinit var clockText: TextView
    private var isFormat1 = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = ClockFragmentBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        clockText = binding.fragClockTv
        coroutineScope = CoroutineScope(Dispatchers.Main)
        startTime()
        clockText.setOnClickListener {
            isFormat1= !isFormat1
            updateTime()
        }



    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu,menu)
    }

    override fun onResume() {
        super.onResume()

        if(!::coroutineScope.isInitialized|| !coroutineScope.isActive){
            startTime()
        }

    }



    override fun onStop() {
        super.onStop()
        try {
            coroutineScope.cancel()
        }catch (e:CancellationException){
            Log.d("Digital Clock","coroutine cancellation error:${e.message}")
        }

    }

    private fun startTime(){
        coroutineScope.launch {
            while(isActive) {
                updateTime()
                delay(100);
            }
        }
    }


    private fun updateTime(){

        val calendar = Calendar.getInstance();
        val hourFormat = SimpleDateFormat("HH");
        val minuteFormat = SimpleDateFormat("mm");
        val secondFormat = SimpleDateFormat("ss");
        val hour = hourFormat.format(calendar.time);
        val minute = minuteFormat.format(calendar.time);
        val second = secondFormat.format(calendar.time);

        if(isFormat1){
            val timeString = "$hour:$minute";

            val spannableString = SpannableString(timeString);

            spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#FA7000")),0,hour.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            spannableString.setSpan(ForegroundColorSpan(Color.WHITE),hour.length+1,(hour.length+1+minute.length),Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

            clockText.text = spannableString;


        }else{
            val timeString = "$hour:$minute:$second";

            val spannableString = SpannableString(timeString);

            spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#FA7000")),0,hour.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            spannableString.setSpan(ForegroundColorSpan(Color.WHITE),hour.length+1,(hour.length+1+minute.length),Spannable.SPAN_EXCLUSIVE_INCLUSIVE)

            spannableString.setSpan(ForegroundColorSpan(Color.parseColor("#FA7000")),(hour.length+minute.length+2),timeString.length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            clockText.text = spannableString;
        }

    }
}