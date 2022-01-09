package com.example.bob_friend_android.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.App
import com.example.bob_friend_android.adapter.BoardAdapter
import com.example.bob_friend_android.model.Board
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.FragmentListBinding
import com.example.bob_friend_android.viewmodel.ListViewModel
import java.util.*


class ListFragment : Fragment() {
    private lateinit var binding: FragmentListBinding
    private lateinit var viewModel: ListViewModel

    private lateinit var  getListResultLauncher: ActivityResultLauncher<Intent>

    private lateinit var boardAdapter: BoardAdapter
    private var boardArrayList : ArrayList<Board> = ArrayList()
    private var listPage = 0 // 현재 페이지

    var toast: Toast? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list, container, false)
        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        binding.lifecycleOwner = this
        binding.list = viewModel


        val swipe = binding.listSwipe
        swipe.setOnRefreshListener {
            listPage = 0
            boardArrayList.clear()
            viewModel.setList(listPage)
            swipe.isRefreshing = false
        }

        binding.recyclerview.layoutManager = LinearLayoutManager(requireActivity())
        boardAdapter = BoardAdapter()
        binding.recyclerview.adapter = boardAdapter

        viewModel.setList(listPage)

        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.mainToolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        getListResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            if(result.resultCode == AppCompatActivity.RESULT_OK) {
                if(result.data != null) {
                    val callType = result.data?.getStringExtra("CallType")
                    if (callType == "delete" || callType == "close"){
                        listPage = 0
                        boardArrayList.clear()
                        viewModel.setList(listPage)
                    }
                }
            }
        }

        binding.recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 스크롤이 끝에 도달했는지 확인
                if (!binding.recyclerview.canScrollVertically(1)) {
                    listPage++
                    viewModel.setList(listPage)
                }
            }
        })

        boardAdapter.setOnItemClickListener(object : BoardAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: Board, pos: Int) {
                activity?.let {
                    val intent = Intent(context, DetailBoardActivity::class.java)
                    intent.putExtra("boardId", data.id)
                    intent.putExtra("userId", data.author!!.id)
                    getListResultLauncher.launch(intent)
                }
            }
        })

        binding.mainToolbar.setOnClickListener {
            activity?.let {
                val intent = Intent(context, BoardSearchActivity::class.java)
                getListResultLauncher.launch(intent)
            }

        }

        observeData()

        return binding.root
    }


    @SuppressLint("ShowToast")
    private fun showToast(msg: String) {
        if (toast == null) {
            toast = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT)
        } else toast?.setText(msg)
        toast?.show()
    }


    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(viewLifecycleOwner) {
                showToast(it)
                if (it == "Forbidden") {
                    viewModel.refreshToken(App.prefs.getString("token", "")!!, App.prefs.getString("refresh", "")!!)
                }
            }

            boardList.observe(viewLifecycleOwner) {
                for(document in it) {
                    boardArrayList.add(document)
                }
                boardAdapter.addItems(boardArrayList)
            }

            refreshToken.observe(viewLifecycleOwner) {
                val editor = App.prefs.edit()
                editor.putString("token", it.accessToken)
                editor.putString("refresh", it.refreshToken)
                editor.putBoolean("checked", true)
                editor.apply()
            }

            val dialog = LoadingDialog(requireContext())
            progressVisible.observe(viewLifecycleOwner) {
                if (progressVisible.value!!) {
                    dialog.show()
                }
                else if (!progressVisible.value!!) {
                    dialog.dismiss()
                }
            }
        }
    }
}