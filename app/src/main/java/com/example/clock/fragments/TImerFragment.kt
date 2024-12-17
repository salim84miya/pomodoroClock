package com.example.clock.fragments

import android.app.AlertDialog
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.clock.R
import com.example.clock.databinding.TimerFragmentBinding
import com.example.clock.utility.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TImerFragment : Fragment(R.layout.timer_fragment) {

    private lateinit var binding: TimerFragmentBinding
    private var timerJob: Job? = null
    private var elapsedSeconds: Int = 0;
    private var isProductive:Boolean = true;
    private lateinit var mediaPlayer: MediaPlayer;
    private var productiveTime:Int = 0;
    private var breakTime:Int = 0;

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = TimerFragmentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        //initial timer value setting
        productiveTime = MyApp.sharedPref.getInt("focus",25)
        binding.timerClockTv.text = formatTime(productiveTime*60)

        binding.timerClockTv.setOnLongClickListener {
            showSelectTime()
            true
        }

        binding.timerClockTv.setOnClickListener {
           startPomodoro()
        }

        binding.resetBtn.setOnClickListener {
            resetTimer()
        }

        binding.endBtn.setOnClickListener {
            endSession()
        }

    }

    private fun endSession() {
        resetTimer()
        isProductive = true
        binding.timerBorder.setImageResource(R.drawable.circle_border)
        binding.resetBtn.setBackgroundColor(resources.getColor(R.color.orange))

        mediaPlayerStopping()

        binding.endBtn.visibility = View.GONE
        binding.resetBtn.visibility = View.GONE
    }

    private fun startPomodoro(){

        val sharedPref = MyApp.sharedPref


        productiveTime = sharedPref.getInt("focus", 26);
        breakTime = sharedPref.getInt("rest", 12);

        Log.d("SharedPreferences", "minutes:$productiveTime seconds:$breakTime ")

        if (timerJob == null) {
            startTimer(if (isProductive) productiveTime else breakTime)
        } else {
            stopTimer()
        }
    }

    private fun startTimer(duration: Int) {

        //converting selection into minutes
         val time = duration * 60;

        Log.d("Productive Timer", "productive start timer method call")

        binding.endBtn.visibility = View.VISIBLE
        binding.resetBtn.visibility = View.VISIBLE

        if(isProductive){
            binding.timerBorder.setImageResource(R.drawable.circle_border)
            binding.resetBtn.setBackgroundColor(resources.getColor(R.color.orange))
        }else{
            binding.timerBorder.setImageResource(R.drawable.circle_border_blue)
            binding.resetBtn.setBackgroundColor(resources.getColor(R.color.blue))
        }

        timerJob = viewLifecycleOwner.lifecycleScope.launch {
            while (elapsedSeconds < (time)) {
                elapsedSeconds++;
                binding.timerClockTv.text = formatTime((time-elapsedSeconds))
                delay(1000)
            }
            buzzerSound()
            stopTimer()

            if(isProductive){
                isProductive = false
                elapsedSeconds=0;
                val breakDuration = MyApp.sharedPref.getInt("rest",6)
                startTimer(breakDuration)
            }else{
                binding.endBtn.visibility = View.GONE
                showRestartDialog()
            }


        }

    }

    private fun stopTimer() {
        Log.d("Timer", "stop timer method call")
        timerJob?.cancel()
        timerJob = null
    }

    private fun resetTimer() {
        stopTimer()
        elapsedSeconds = 0;

        binding.timerClockTv.text = if (isProductive) formatTime(productiveTime*60) else formatTime(breakTime*60);
    }

    override fun onDestroy() {
        super.onDestroy()
        stopTimer()

        try {
            mediaPlayer.release()
        }catch (e:Exception){
            Log.e("MediaPlayer","Error Releasing media player")
        }
    }

    private fun formatTime(time: Int): String {
        val minutes = time / 60;
        val seconds = time % 60;
        return String.format("%02d:%02d", minutes, seconds)
    }

    private suspend fun buzzerSound(){
       val job = viewLifecycleOwner.lifecycleScope.launch {

           if(::mediaPlayer.isInitialized){
               mediaPlayer.prepare()
               mediaPlayer.start()
           }else{
               mediaPlayer = MediaPlayer.create(requireContext(),R.raw.buzzing_sound)
               mediaPlayer.start()
           }

            binding.resetBtn.isClickable =  false
            binding.timerClockTv.isClickable = false
            delay(3000)

           mediaPlayerStopping()

           binding.resetBtn.isClickable =  true
           binding.timerClockTv.isClickable = true
        }
        job.join()


    }

    private fun showRestartDialog(){

        elapsedSeconds =0;
        isProductive = true

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.timer_alert_dialog,null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)


        val restartBtn = dialogView.findViewById<Button>(R.id.restart_btn)
        val closeBtn = dialogView.findViewById<Button>(R.id.close_btn)

        restartBtn.setOnClickListener {
            startTimer(MyApp.sharedPref.getInt("focus",16))
            dialog.dismiss()
        }

        closeBtn.setOnClickListener {
            binding.timerBorder.setImageResource(R.drawable.circle_border)
            binding.resetBtn.setBackgroundColor(resources.getColor(R.color.orange))
            resetTimer()
            dialog.dismiss()
        }

        dialog.show()

    }

    private fun showSelectTime(){

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.time_select_dialog,null)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val focusTimePicker = dialogView.findViewById<NumberPicker>(R.id.focus_time)
        val breakTimePicker = dialogView.findViewById<NumberPicker>(R.id.break_time)

        focusTimePicker.minValue = 1
        focusTimePicker.maxValue =90

        breakTimePicker.minValue = 1
        breakTimePicker.maxValue = 90

        val saveBtn = dialogView.findViewById<Button>(R.id.save_btn)
        val cancelBtn = dialogView.findViewById<Button>(R.id.cancel_btn)

        saveBtn.setOnClickListener {

            with(MyApp.sharedPref.edit()){
                putInt("focus",focusTimePicker.value)
                putInt("rest",breakTimePicker.value)
                apply()
            }
            binding.timerBorder.setImageResource(R.drawable.circle_border)
            binding.resetBtn.setBackgroundColor(resources.getColor(R.color.orange))
            binding.endBtn.visibility = View.GONE
            resetTimer()
            dialog.dismiss()
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }


    private fun mediaPlayerStopping(){

        if(this::mediaPlayer.isInitialized && mediaPlayer.isPlaying){
            mediaPlayer.stop()
        }
    }

}






