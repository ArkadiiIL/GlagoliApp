package com.arkadii.glagoli.alarmmenu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.arkadii.glagoli.databinding.FragmentAlarmMenuBinding
import com.arkadii.glagoli.record.RecordFragment

class AlarmMenuFragment: Fragment() {
    private var _binding: FragmentAlarmMenuBinding? = null
    private val binding get() = _binding ?: error("NullPointerException in $TAG")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        Log.v(TAG, "Init binding in AlarmMenuFragment")
        _binding = FragmentAlarmMenuBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(RecordFragment.TAG, "onDestroyView in ${RecordFragment.TAG}")
        _binding = null
    }

    companion object {
        const val TAG = "AlarmMenuFragment"
    }
}