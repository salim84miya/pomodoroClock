package com.example.clock.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.clock.fragments.ClockFragment
import com.example.clock.fragments.TImerFragment

class ClockStateAdapter(fragment: FragmentActivity):FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int =2;

    override fun createFragment(position: Int): Fragment {

        return  when(position){
            0-> ClockFragment()
            1->TImerFragment()
            else-> throw IllegalStateException("unexpected position $position")
        }
    }
}