package com.example.clock.viewmodel

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.clock.R
import com.example.clock.utility.MyApp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TimerViewmodel(application:Application):AndroidViewModel(application) {


    private val context:Context = application.applicationContext

    private var _elapsedSeconds =    MutableLiveData<Int>()
    val elapsedSeconds:LiveData<Int> = _elapsedSeconds;

    private var _productiveTime = MutableLiveData<Int>()
    val productiveTime :LiveData<Int> = _productiveTime

    private var _breakTime = MutableLiveData<Int>()
    val breakTime:LiveData<Int> = _breakTime

    private var _progress = MutableLiveData<Int>()
    val progress:LiveData<Int> = _progress

    private var _buttonVisibility = MutableLiveData<Pair<Boolean,Boolean>>()
    val buttonVisibility :LiveData<Pair<Boolean,Boolean>> = _buttonVisibility

    private var _borderColor = MutableLiveData<Int>()
    val borderColor:LiveData<Int> = _borderColor

    private var _formatedTime = MutableLiveData<String>()
    val formatedTime:LiveData<String> = _formatedTime

    private var _timerJob = MutableLiveData<Job?>()
    val timerJob:LiveData<Job?> = _timerJob

    private var mediaPlayer:MediaPlayer? = null

    private var _isBuzzerPlaying = MutableLiveData<Boolean>()
    val isBuzzerPlaying:LiveData<Boolean> = _isBuzzerPlaying

    private var _isShowRestartDialog = MutableLiveData<Boolean>()
    val isShowRestartDialog:LiveData<Boolean> = _isShowRestartDialog

    private var _focusTimerColor = MutableLiveData<Int>()
    val focusTimerColor:LiveData<Int> = _focusTimerColor

    private var _breakTimerColor = MutableLiveData<Int>()
    val breakTimerColor:LiveData<Int> = _breakTimerColor

    private var _isProductiveValue = MutableLiveData(true)
    val isProductive:LiveData<Boolean> = _isProductiveValue;

    init {
        _productiveTime.value = MyApp.sharedPref.getInt("focus",600);
        _breakTime.value = MyApp.sharedPref.getInt("rest",300);


        Log.d("Timer","_productiveTime.value ${_productiveTime.value}")
        Log.d("Timer","productiveTime.value ${productiveTime.value}")
        Log.d("Timer","_breakTime.value ${_breakTime.value}")
        Log.d("Timer","breakTime.value ${breakTime.value}")

        _focusTimerColor.value = MyApp.sharedPref.getInt("focus_timer_color",Color.argb(250, 112, 0, 0))
        _breakTimerColor.value = MyApp.sharedPref.getInt("break_timer_color",Color.argb(255, 40, 186, 251))

        _borderColor.value = _focusTimerColor.value

        _elapsedSeconds.value = 0;

        _isShowRestartDialog.value = false

        _formatedTime.value = formatTime(_productiveTime.value ?: 0)

        _progress.value = 100
    }

    fun toggleProductiveState(value:Boolean){
        _isProductiveValue.value = value
    }


    fun changeTimerTime(focusTime:Int,breakTime:Int){
            MyApp.sharedPref.edit()
                .putInt("focus",focusTime)
                .putInt("break",breakTime)
                .apply()

        _productiveTime.value = focusTime
        _breakTime.value = breakTime

    }

    private fun incrementElapsedSeconds(){
        _elapsedSeconds.value = (_elapsedSeconds.value ?:0)+1;
    }

    fun resetElapsedSeconds(){
        _elapsedSeconds.value =0;
    }

    private fun formatTime(time: Int): String {
        val minutes = time / 60;
        val seconds = time % 60;
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun runningTimer(time:Int){

        _buttonVisibility.value = Pair(true,true)

        if(isProductive.value != false){
            _borderColor.value = _focusTimerColor.value
        }else{
            _borderColor.value = _breakTimerColor.value
        }

        _timerJob.value = viewModelScope.launch {
            _progress.value = 100

            while ((elapsedSeconds.value?:0)<time){
                val remainingTime = time-(elapsedSeconds.value ?:0)

                _progress.value = ((remainingTime*100)/time).toInt()
                incrementElapsedSeconds()
                _formatedTime.value = formatTime(time-(elapsedSeconds.value?:0))

                delay(1000)
            }

           _progress.value = 0
            buzzerSound()
            stopTimer()

            if(isProductive.value==true){
                toggleProductiveState(false)
                resetElapsedSeconds()
                runningTimer(breakTime.value ?: 0)
            }else{
                _buttonVisibility.value = Pair(false,false)
                _isShowRestartDialog.value = true
            }
        }
    }

     fun stopTimer(){
        Log.d("Timer", "stop timer method call")
        _timerJob.value?.cancel()
        _timerJob.value = null
    }

     fun resetTimer(){
        stopTimer()
        resetElapsedSeconds()
        _formatedTime.value = if(isProductive.value != false) formatTime(productiveTime.value ?: 600) else formatTime(breakTime.value ?: 300)
         _progress.value = 100
    }

    fun muteTimer(){
        stoppingBuzzerSound()
    }

    private suspend fun buzzerSound(){

        val job = viewModelScope.launch {

            if(_isBuzzerPlaying.value == true){
                mediaPlayer?.reset()
            }else{
                mediaPlayer = MediaPlayer.create(context,R.raw.buzzing_sound)
            }

            mediaPlayer?.start()

            _isBuzzerPlaying.value = true

            delay(3000)

            stoppingBuzzerSound()

            _isBuzzerPlaying.value = false
        }
        job.join()

    }

    private fun stoppingBuzzerSound(){
        mediaPlayer?.takeIf {it.isPlaying }?.apply {
            stop()
            release()
        }
        mediaPlayer = null
    }

    override fun onCleared() {
        super.onCleared()
        stoppingBuzzerSound()
    }

    fun endSession(){
        resetTimer()
        toggleProductiveState(true)
        _borderColor.value = _focusTimerColor.value
        stoppingBuzzerSound()
        _buttonVisibility.value = Pair(false,false)
    }

    fun toggleBorder(border:Int){
        _borderColor.value = border
    }

    fun toggleProgress(progress:Int){
        _progress.value = progress
    }


    fun toggleButtonVisibility(btnVisibility:Boolean){
        _buttonVisibility.value = Pair(btnVisibility,btnVisibility)
    }

    fun toggleRestartDialogDisplaying(show:Boolean){
        _isShowRestartDialog.value = show
    }

    fun toggleFocusTimerColor(color:Int){
        _focusTimerColor.value = color
        toggleBorder(color)
        MyApp.sharedPref.edit()
            .putInt("focus_timer_color",color)
            .apply()
    }

    fun toggleBreakTimerColor(color:Int){
        _breakTimerColor.value = color

        MyApp.sharedPref.edit()
            .putInt("break_timer_color",color)
            .apply()
    }
}