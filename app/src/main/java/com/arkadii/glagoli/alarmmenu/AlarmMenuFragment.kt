package com.arkadii.glagoli.alarmmenu

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.arkadii.glagoli.data.AlarmViewModel
import com.arkadii.glagoli.databinding.FragmentAlarmMenuBinding
import com.arkadii.glagoli.record.MediaPlayerManager
import com.arkadii.glagoli.record.RecordFragment

class AlarmMenuFragment: Fragment() {
    private var _binding: FragmentAlarmMenuBinding? = null
    private val binding get() = _binding ?: error("NullPointerException in $TAG")
    private lateinit var alarmMenuAdapter: AlarmMenuAdapter
    private lateinit var alarmViewModel: AlarmViewModel
    private lateinit var mediaPlayerManager: MediaPlayerManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "Create AlarmMenuAdapter")
        alarmViewModel = ViewModelProvider.AndroidViewModelFactory(
            requireActivity().application).create(AlarmViewModel::class.java)
        mediaPlayerManager = MediaPlayerManager()
        alarmMenuAdapter = AlarmMenuAdapter(
            requireContext(),
            alarmViewModel,
            childFragmentManager,
            mediaPlayerManager)
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        Log.v(TAG, "Init binding in AlarmMenuFragment")
        _binding = FragmentAlarmMenuBinding.inflate(inflater, container, false)

        binding.rvAlarmMenu.adapter = alarmMenuAdapter
        binding.rvAlarmMenu.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAlarmMenu.
        addItemDecoration(DividerItemDecoration(requireContext(),DividerItemDecoration.VERTICAL))

        alarmViewModel.getAllAlarms.observe(requireActivity()) {
            Log.d(TAG, "Update data in AlarmMenuAdapter")
            alarmMenuAdapter.setData(it)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d(RecordFragment.TAG, "onDestroyView in ${RecordFragment.TAG}")
        _binding = null
        mediaPlayerManager.closeMediaPlayer()
    }

    companion object {
        const val TAG = "AlarmMenuFragment"
    }
}