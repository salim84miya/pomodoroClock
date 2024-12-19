package com.example.clock.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.clock.R
import com.example.clock.databinding.HelpFragmentBinding

class HelpFragment :Fragment(R.layout.help_fragment){

    private lateinit var binding:HelpFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = HelpFragmentBinding.inflate(layoutInflater,container,false)
        return binding.root
    }

}