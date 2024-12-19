package com.example.clock.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ClockViewmodel:ViewModel() {

    private var isFormatPrimaryValue = MutableLiveData(true )
    val isFormatPrimary:LiveData<Boolean> = isFormatPrimaryValue


    fun changeFormat(){
        isFormatPrimaryValue.value = !(isFormatPrimaryValue.value?:true)
    }
}