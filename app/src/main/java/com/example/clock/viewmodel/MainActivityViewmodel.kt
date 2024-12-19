package com.example.clock.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainActivityViewmodel:ViewModel() {

    private var _showViewpager = MutableLiveData<Boolean>()
    val showViewpager:LiveData<Boolean> = _showViewpager

    init {
        _showViewpager.value = true
    }

    fun toggleViewpagerVisibility(visibility:Boolean){
        _showViewpager.value = visibility
    }
}