package com.example.bob_friend_android.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.R
import com.example.bob_friend_android.ui.adapter.BoardAdapter
import com.example.bob_friend_android.databinding.FragmentMyboardBinding
import com.example.bob_friend_android.model.Board
import com.example.bob_friend_android.viewmodel.ListViewModel
import java.util.ArrayList

class MyBoardFragment: Fragment() {

    var toast: Toast? = null

    private lateinit var binding: FragmentMyboardBinding
    private lateinit var viewModel: ListViewModel
    private var listPage = 0 // 현재 페이지

    private var type : String = ""

    private lateinit var boardAdapter: BoardAdapter
    private var boardArrayList : ArrayList<Board> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_myboard, container, false)
        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        binding.lifecycleOwner = this
        binding.list = viewModel

        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.myboardToolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

//        binding.myboardToolbar.navigationIcon?.apply {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                colorFilter = BlendModeColorFilter(Color.WHITE, BlendMode.SRC_IN)
//            }else{
//                setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
//            }
//        }

//        binding.recyclerviewBoard.layoutManager = LinearLayoutManager(requireContext())
//        boardAdapter = BoardAdapter()
//        binding.recyclerviewBoard.adapter = boardAdapter

//        if(intent.hasExtra("type")){
//            type = intent.getStringExtra("type").toString()
//
//            viewModel.setList(type = type, listPage = listPage)
//
//            binding.recyclerviewBoard.addOnScrollListener(object : RecyclerView.OnScrollListener(){
//                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                    super.onScrolled(recyclerView, dx, dy)
//                    // 스크롤이 끝에 도달했는지 확인
//                    if (!binding.recyclerviewBoard.canScrollVertically(1)) {
//                        listPage++
//                        viewModel.setList(type = type, listPage = listPage)
//                    }
//                }
//            })
//        }

        boardAdapter.setOnItemClickListener(object : BoardAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: Board, pos: Int) {
                val intent = Intent(requireContext(), DetailBoardActivity::class.java)
                intent.putExtra("boardId", data.id)
                intent.putExtra("userId", data.author!!.id)
                startActivity(intent)
            }
        })

        observeData()

        return binding.root
    }
//
//    override fun onContextItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            android.R.id.home -> {
//                finish()
//                return true
//            }
//        }
//        return super.onContextItemSelected(item)
//    }

    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(viewLifecycleOwner) {
//                showToast(it)
            }

            boardList.observe(viewLifecycleOwner) {
                for(document in it.boardList) {
                    boardArrayList.add(document)
                }
                boardAdapter.addItems(boardArrayList)
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

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val id = item.itemId
//        when (id) {
//            android.R.id.home -> {
//                finish()
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }

//    @SuppressLint("ShowToast")
//    private fun showToast(msg: String) {
//        if (toast == null) {
//            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
//        } else toast?.setText(msg)
//        toast?.show()
//    }
}