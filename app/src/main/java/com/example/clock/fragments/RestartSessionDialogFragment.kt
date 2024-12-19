package com.example.clock.fragments

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.example.clock.R
import com.example.clock.viewmodel.TimerViewmodel
import com.example.clock.viewmodelfactory.TimerViewmodelFactory

class RestartSessionDialogFragment:DialogFragment() {

    private lateinit var viewmodel: TimerViewmodel

    interface SessionSelectionListener{
        fun restartSelection()
        fun cancelSelection()
    }

    private var listener: SessionSelectionListener?= null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        val parentFragment = parentFragment
        if(parentFragment is SessionSelectionListener){
           listener = parentFragment
        }else{
            throw ClassCastException("$context must implement SessionSelectionListener")
        }

        viewmodel = ViewModelProvider(requireActivity(),TimerViewmodelFactory(requireActivity().application))[TimerViewmodel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.timer_alert_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val restartBtn = view.findViewById<Button>(R.id.restart_btn)
        val closeBtn = view.findViewById<Button>(R.id.close_btn)

        restartBtn.setOnClickListener {
            listener?.restartSelection()
            viewmodel.toggleRestartDialogDisplaying(false)
            dismiss()
        }

        closeBtn.setOnClickListener {
            listener?.cancelSelection()
            viewmodel.toggleRestartDialogDisplaying(false)
            dismiss()
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onStart() {
        super.onStart()

        val dialogWindow = dialog?.window

        dialogWindow?.let {

            isCancelable = false
            dialog?.setCanceledOnTouchOutside(false)
        }
    }
}