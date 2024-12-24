package com.example.clock.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.clock.R
import com.example.clock.databinding.HelpFragmentBinding
import com.example.clock.viewmodel.TimerViewmodel
import com.example.clock.viewmodelfactory.TimerViewmodelFactory

class HelpFragment :Fragment(R.layout.help_fragment){

    private lateinit var binding:HelpFragmentBinding
    private lateinit var timerViewmodel: TimerViewmodel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = HelpFragmentBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timerViewmodel = ViewModelProvider(requireActivity(),TimerViewmodelFactory(requireActivity().application))[TimerViewmodel::class.java]

        if (timerViewmodel.timerJob.value == null) {

        } else {
            timerViewmodel.endSession()
        }
    }
}