package com.example.bob_friend_android.view.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.adapter.BoardAdapter
import com.example.bob_friend_android.model.Board
import com.example.bob_friend_android.R
import com.example.bob_friend_android.adapter.SearchAdapter
import com.example.bob_friend_android.databinding.FragmentListBinding
import com.example.bob_friend_android.model.SearchLocation
import com.example.bob_friend_android.viewmodel.ListViewModel
import java.util.*


class ListFragment : Fragment() {
    private lateinit var binding: FragmentListBinding
    private lateinit var viewModel: ListViewModel

    private lateinit var boardAdapter: BoardAdapter
    private var boardList : ArrayList<Board> = ArrayList()
    private var listPage = 0 // 현재 페이지
    var count = 0 //스크롤 하단

    //약속 검색 기능
    private val searchItems = arrayListOf<SearchLocation>()   // 리사이클러 뷰 아이템
    private val searchAdapter = SearchAdapter(searchItems)    // 리사이클러 뷰 어댑터
    private var searchPage = 1      // 검색 페이지 번호
    private var keyword = ""        // 검색 키워드


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false)
        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        binding.lifecycleOwner = this
        binding.list = viewModel


        val swipe = binding.listSwipe
        swipe.setOnRefreshListener {
            listPage = 0
            boardList.clear()
            viewModel.setList(binding.recyclerview.adapter as BoardAdapter, requireContext(), listPage, boardList)
            swipe.isRefreshing = false
        }

        binding.searchList.adapter = searchAdapter
        binding.searchList.visibility = View.GONE

        binding.recyclerview.layoutManager = LinearLayoutManager(requireActivity())
        boardAdapter = BoardAdapter(requireActivity())
        binding.recyclerview.adapter = boardAdapter

        viewModel.setList(binding.recyclerview.adapter as BoardAdapter,requireContext(), listPage, boardList)

        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.mainToolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        binding.mainEditTextSearch.visibility = View.INVISIBLE
        binding.search.setOnClickListener {
            binding.mainEditTextSearch.visibility = View.VISIBLE

            keyword = binding.mainEditTextSearch.text.toString()
            searchPage = 1

            hideKeyboard()
        }

        binding.recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 스크롤이 끝에 도달했는지 확인
                if (!binding.recyclerview.canScrollVertically(1)) {
                    listPage++
                    viewModel.setList(binding.recyclerview.adapter as BoardAdapter, requireContext(), listPage, boardList)
                }
            }
        })

        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.mainToolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        }


        return binding.root
    }

    private fun hideKeyboard(){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.mainEditTextSearch.windowToken, 0)
    }


    private fun refreshAdapter() {
        binding.recyclerview.adapter?.notifyDataSetChanged()
    }

    override fun onResume() {
        super.onResume()
        refreshAdapter()
    }
}