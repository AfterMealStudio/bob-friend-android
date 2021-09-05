package com.example.bob_friend_android.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bob_friend_android.R
import com.example.bob_friend_android.adapter.BoardAdapter
import com.example.bob_friend_android.databinding.FragmentMyappointmentBinding
import com.example.bob_friend_android.model.Board
import com.example.bob_friend_android.viewmodel.ListViewModel
import java.util.ArrayList

class MyAppointmentFragment : Fragment(){
    private lateinit var binding: FragmentMyappointmentBinding
    private lateinit var viewModel: ListViewModel
    private val boardList : ArrayList<Board> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_myappointment, container, false)
        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        binding.lifecycleOwner = this
        binding.list = viewModel

        binding.recyclerviewAppointment.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerviewAppointment.adapter = BoardAdapter(requireActivity(), boardList)

        viewModel.setMyAppointment(binding.recyclerviewAppointment,requireContext())

        return binding.root
    }
}