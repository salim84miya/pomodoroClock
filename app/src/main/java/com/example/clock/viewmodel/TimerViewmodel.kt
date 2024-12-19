package com.example.clock.viewmodel

import android.app.Application
import android.content.Context
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

    private var _timeLeft = MutableLiveData<Int>()
    val timeLeft:LiveData<Int> = _timeLeft

    private var _buttonVisibility = MutableLiveData<Pair<Boolean,Boolean>>()
    val buttonVisibility :LiveData<Pair<Boolean,Boolean>> = _buttonVisibility

    private var _borderColor = MutableLiveData<Int>()
    val borderColor:LiveData<Int> = _borderColor

    private var _formatedTime = MutableLiveData<String>()
    val formatedTime:LiveData<String> = _formatedTime

    private var _resetButtonColor = MutableLiveData<Int>()
    val resetButtonColor:LiveData<Int> = _resetButtonColor

    private var _timerJob = MutableLiveData<Job?>()
    val timerJob:LiveData<Job?> = _timerJob

    private var mediaPlayer:MediaPlayer? = null

    private var _isBuzzerPlaying = MutableLiveData<Boolean>()
    val isBuzzerPlaying:LiveData<Boolean> = _isBuzzerPlaying

    private var _isShowRestartDialog = MutableLiveData<Boolean>()
    val isShowRestartDialog:LiveData<Boolean> = _isShowRestartDialog

    init {
        _productiveTime.value = MyApp.sharedPref.getInt("focus",25);
        _breakTime.value = MyApp.sharedPref.getInt("rest",5);
        _elapsedSeconds.value = 0;
        _isShowRestartDialog.value = false
        _formatedTime.value = productiveTime.value?.let { formatTime(it) }
    }

    private var _isProductiveValue = MutableLiveData(true)
    val isProductive:LiveData<Boolean> = _isProductiveValue;


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

    fun incrementElapsedSeconds(){
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
            _borderColor.value = R.drawable.circle_border
            _resetButtonColor.value = R.color.orange
        }else{
            _borderColor.value = R.drawable.circle_border_blue
            _resetButtonColor.value = R.color.blue
        }

        _timerJob.value = viewModelScope.launch {
            while ((elapsedSeconds.value?:0)<time){
                incrementElapsedSeconds()
                _formatedTime.value = formatTime(time-(elapsedSeconds.value?:0))
                delay(1000)
            }
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
        _formatedTime.value = if(isProductive.value != false) formatTime(productiveTime.value ?: 3) else formatTime(breakTime.value ?: 2)
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
//        try {
//            mediaPlayer?.release()
//        }catch (e:Exception){
//            Log.e("MediaPlayer","Error Releasing media player")
//        }

        mediaPlayer = null
    }

    override fun onCleared() {
        super.onCleared()
        stoppingBuzzerSound()
    }

    fun endSession(){
        resetTimer()
        toggleProductiveState(true)
        _borderColor.value = R.drawable.circle_border
        _resetButtonColor.value = R.color.orange
        stoppingBuzzerSound()
        _buttonVisibility.value = Pair(false,false)
    }

    fun toggleBorder(border:Int){
        _borderColor.value = border
    }

    fun toggleResetButtonColor(buttonColor:Int){
        _resetButtonColor.value = buttonColor
    }

    fun toggleButtonVisibility(btnVisibility:Boolean){
        _buttonVisibility.value = Pair(btnVisibility,btnVisibility)
    }

    fun toggleRestartDialogDisplaying(show:Boolean){
        _isShowRestartDialog.value = show
    }
}