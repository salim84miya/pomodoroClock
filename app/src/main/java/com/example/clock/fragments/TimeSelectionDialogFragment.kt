package com.example.clock.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.NumberPicker
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.clock.R
import com.example.clock.viewmodel.TimerViewmodel
import com.example.clock.viewmodelfactory.TimerViewmodelFactory

class TimeSelectionDialogFragment:DialogFragment() {

    private lateinit var viewmodel: TimerViewmodel

    interface TimeSelectedListener{
        fun onTimeSelected()
    }

    private var listener:TimeSelectedListener?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.time_select_dialog, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val focusTimePicker = view.findViewById<NumberPicker>(R.id.focus_time)
        val breakTimePicker = view.findViewById<NumberPicker>(R.id.break_time)

        focusTimePicker.minValue = 1
        focusTimePicker.maxValue =90

        viewmodel.productiveTime.observe(viewLifecycleOwner, Observer {
            focusTimePicker.value = it/60;
        })

        breakTimePicker.minValue = 1
        breakTimePicker.maxValue = 90
        viewmodel.breakTime.observe(viewLifecycleOwner, Observer {
            breakTimePicker.value = it/60;
        })

        val saveBtn = view.findViewById<Button>(R.id.save_btn)
        val cancelBtn = view.findViewById<Button>(R.id.cancel_btn)

        saveBtn.setOnClickListener {

            val focusTime = (focusTimePicker.value)*60;
            val breakTime = (breakTimePicker.value)*60;

            viewmodel.changeTimerTime(focusTime,breakTime)
            listener?.onTimeSelected()
            dismiss()
        }

        cancelBtn.setOnClickListener {
            dismiss()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val parentFragment = parentFragment

        if(parentFragment is TimeSelectedListener){
            listener = parentFragment
        }else{
            throw ClassCastException("$context must implement TimeSelectedListener")
        }
        viewmodel = ViewModelProvider(requireActivity(),TimerViewmodelFactory(requireActivity().application))[TimerViewmodel::class.java]
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }
}