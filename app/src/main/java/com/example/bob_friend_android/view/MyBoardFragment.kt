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
import com.example.bob_friend_android.databinding.FragmentListBinding
import com.example.bob_friend_android.databinding.FragmentMyboardBinding
import com.example.bob_friend_android.model.Board
import com.example.bob_friend_android.viewmodel.ListViewModel
import java.util.ArrayList

class MyBoardFragment : Fragment() {
    private lateinit var binding: FragmentMyboardBinding
    private lateinit var viewModel: ListViewModel
    private val boardList: ArrayList<Board> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_myboard, container, false)
        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        binding.lifecycleOwner = this
        binding.list = viewModel

        binding.recyclerviewBoard.layoutManager = LinearLayoutManager(requireActivity())
        binding.recyclerviewBoard.adapter = BoardAdapter()

//        viewModel.setMyBoard(binding.recyclerviewBoard, requireContext())

        return binding.root
    }
}