package com.example.clock.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.clock.R
import com.example.clock.databinding.TimerFragmentBinding
import com.example.clock.viewmodel.TimerViewmodel
import com.example.clock.viewmodelfactory.TimerViewmodelFactory

class TImerFragment : Fragment(R.layout.timer_fragment), TimeSelectionDialogFragment.TimeSelectedListener,RestartSessionDialogFragment.SessionSelectionListener {

    private lateinit var binding: TimerFragmentBinding
    private lateinit var timerViewmodel: TimerViewmodel
    private var isProductive:Boolean = true;
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

        timerViewmodel = ViewModelProvider(requireActivity(),TimerViewmodelFactory(requireActivity().application))[TimerViewmodel::class.java]

        timerViewmodel.isProductive.observe(viewLifecycleOwner, Observer {
            isProductive = it
        })

        timerViewmodel.productiveTime.observe(viewLifecycleOwner, Observer {
            productiveTime = it
        })

        timerViewmodel.breakTime.observe(viewLifecycleOwner, Observer {
            breakTime = it
        })

        timerViewmodel.formatedTime.observe(viewLifecycleOwner, Observer {
            binding.timerClockTv.text = it
        })

        timerViewmodel.buttonVisibility.observe(viewLifecycleOwner, Observer {
            if(it.first){
                binding.resetBtn.visibility = View.VISIBLE
                binding.endBtn.visibility = View.VISIBLE
            }else{
                binding.resetBtn.visibility = View.GONE
                binding.endBtn.visibility = View.GONE
            }
        })

        timerViewmodel.borderColor.observe(viewLifecycleOwner, Observer {
            binding.timerBorder.setImageResource(it)
        })

        timerViewmodel.resetButtonColor.observe(viewLifecycleOwner, Observer {
            binding.resetBtn.setBackgroundColor(resources.getColor(it))
        })

        timerViewmodel.isShowRestartDialog.observe(viewLifecycleOwner, Observer {
            if(it){
                showRestartDialog()
            }
        })

        binding.timerClockTv.setOnLongClickListener {
            showSelectTime()
            true
        }

        binding.timerClockTv.setOnClickListener {
           startPomodoro()
        }

        binding.resetBtn.setOnClickListener {
            timerViewmodel.resetTimer()
        }

        binding.endBtn.setOnClickListener {
            timerViewmodel.endSession()
        }

        timerViewmodel.isBuzzerPlaying.observe(viewLifecycleOwner, Observer {
            binding.endBtn.isClickable = !it
            binding.resetBtn.isClickable = !it
        })

    }

    private fun startPomodoro(){

        Log.d("SharedPreferences", "minutes:$productiveTime seconds:$breakTime ")

        if (timerViewmodel.timerJob.value == null) {
            timerViewmodel.runningTimer(if(isProductive) productiveTime else breakTime)
        } else {
            timerViewmodel.stopTimer()
        }
    }



    private fun showSelectTime(){
        val dialogFragment = TimeSelectionDialogFragment()
        dialogFragment.show(childFragmentManager,"TimePickerDialog")
    }

    private fun showRestartDialog(){

        timerViewmodel.resetElapsedSeconds()
        timerViewmodel.toggleProductiveState(true)

        if(childFragmentManager.findFragmentByTag("RestartSessionDialog")==null){
            val dialogFragment = RestartSessionDialogFragment()
            dialogFragment.show(childFragmentManager,"RestartSessionDialog")
        }

    }

    override fun onTimeSelected() {
            timerViewmodel.toggleBorder(R.drawable.circle_border)
            timerViewmodel.toggleResetButtonColor(R.color.orange)
             timerViewmodel.endSession()
    }

    override fun restartSelection() {
        timerViewmodel.runningTimer(productiveTime)
    }

    override fun cancelSelection() {
        timerViewmodel.toggleBorder(R.drawable.circle_border)
        timerViewmodel.toggleResetButtonColor(R.color.orange)
        timerViewmodel.toggleButtonVisibility(false)
        timerViewmodel.resetTimer()
    }


}






