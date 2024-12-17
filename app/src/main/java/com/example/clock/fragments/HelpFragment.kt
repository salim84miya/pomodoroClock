package com.example.clock.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStop() {
        super.onStop()

        val viewPager2 = activity?.findViewById<ViewPager2>(R.id.viewpager2)
        val toolbar = activity?.findViewById<Toolbar>(R.id.toolbar)
        viewPager2?.visibility = View.VISIBLE
        toolbar?.visibility = View.VISIBLE
    }

}