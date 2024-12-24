package com.example.clock.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.clock.R
import com.example.clock.databinding.SettingFragmentBinding
import com.example.clock.viewmodel.TimerViewmodel
import com.example.clock.viewmodelfactory.TimerViewmodelFactory
import yuku.ambilwarna.AmbilWarnaDialog
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener

class SettingFragment: Fragment(R.layout.setting_fragment) {

    private lateinit var binding: SettingFragmentBinding
    private lateinit var timerViewmodel: TimerViewmodel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = SettingFragmentBinding.inflate(layoutInflater,container,false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timerViewmodel = ViewModelProvider(requireActivity(),TimerViewmodelFactory(requireActivity().application))[TimerViewmodel::class.java]

        if (timerViewmodel.timerJob.value == null) {

        } else {
            timerViewmodel.endSession()
        }


        timerViewmodel.focusTimerColor.observe(viewLifecycleOwner, Observer {
            binding.focusColorPicker.setIndicatorColor(it)
        })

        timerViewmodel.breakTimerColor.observe(viewLifecycleOwner, Observer {
            binding.breakColorPicker.setIndicatorColor(it)
        })

        binding.focusColorEditBtn.setOnClickListener {
            openColorPickerDialog(true)
        }

        binding.breakColorEditBtn.setOnClickListener {
            openColorPickerDialog(false)
        }
    }

    private fun openColorPickerDialog(isFocus:Boolean) {


        var defaultColor :Int = Color.argb(250, 112, 0, 0)

        if(isFocus){
            timerViewmodel.focusTimerColor.observe(viewLifecycleOwner, Observer {
                defaultColor= it
            })
        }else{
            timerViewmodel.breakTimerColor.observe(viewLifecycleOwner, Observer {
                defaultColor = it;
            })
        }

        val colorPickerDialog = AmbilWarnaDialog(requireActivity(),defaultColor,object:OnAmbilWarnaListener{
            override fun onCancel(dialog: AmbilWarnaDialog?) {

            }

            override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                if(isFocus){
                    timerViewmodel.toggleFocusTimerColor(color)
                }else{
                    timerViewmodel.toggleBreakTimerColor(color)
                }
            }

        })

        colorPickerDialog.show()
    }
}