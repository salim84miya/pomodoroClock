package com.example.clock.viewmodel

import android.content.res.Configuration
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Calendar

class ClockViewmodel:ViewModel() {

    private var isFormatPrimaryValue = MutableLiveData(true )
    val isFormatPrimary:LiveData<Boolean> = isFormatPrimaryValue


    private var _currentDate = MutableLiveData<SpannableString>()
    val currentDate:LiveData<SpannableString> = _currentDate


    fun changeFormat(){
        isFormatPrimaryValue.value = !(isFormatPrimaryValue.value?:true)
    }

    private fun formatDate(date: Int, day: Int, month: Int) {

        val daysOfWeek = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val monthsOfYear = arrayOf("January","February","March","April","May","June","July","August","September","October","November","December")

        val dayOfWeek = daysOfWeek[day]
        val monthOfYear = monthsOfYear[month]

        val currentDateString = "$date ${monthOfYear},${dayOfWeek}"



        // Create a SpannableString to format different parts of the date
        val spannableDateString = SpannableString(currentDateString)

        // Apply color formatting to the day of the month
        spannableDateString.setSpan(
            ForegroundColorSpan(Color.WHITE),
            0,
            date.toString().length, // Format the day of the month (e.g., "23")
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Apply color formatting to the month name
        spannableDateString.setSpan(
            ForegroundColorSpan(Color.WHITE),
            date.toString().length + 1, // Start from the first character of the month
            date.toString().length + 1 + monthOfYear.length,
            Spannable.SPAN_EXCLUSIVE_INCLUSIVE
        )

        // Apply color formatting to the day of the week
        spannableDateString.setSpan(
            ForegroundColorSpan(Color.parseColor("#FA7000")),
            currentDateString.length - dayOfWeek.length, // Start from the beginning of the day of the week
            currentDateString.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        // Set the current date value
        _currentDate.value = spannableDateString

    }

    fun getCurrentDate(){
        val calendar = Calendar.getInstance()
        val month =  calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_WEEK)
        val date = calendar.get(Calendar.DAY_OF_MONTH)

        formatDate(date,day,month)
    }




}