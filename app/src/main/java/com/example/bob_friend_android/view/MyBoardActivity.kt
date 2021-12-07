package com.example.bob_friend_android.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.R
import com.example.bob_friend_android.adapter.BoardAdapter
import com.example.bob_friend_android.databinding.ActivityMyboardBinding
import com.example.bob_friend_android.model.Board
import com.example.bob_friend_android.viewmodel.ListViewModel
import java.util.ArrayList

class MyBoardActivity: AppCompatActivity() {

    var toast: Toast? = null

    private lateinit var binding: ActivityMyboardBinding
    private lateinit var viewModel: ListViewModel
    private var listPage = 0 // 현재 페이지

    private lateinit var boardAdapter: BoardAdapter
    private var boardArrayList : ArrayList<Board> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_myboard)
        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        binding.lifecycleOwner = this
        binding.list = viewModel

        setSupportActionBar(binding.myboardToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.myboardToolbar.title = "내가 만든 약속"

        binding.recyclerviewBoard.layoutManager = LinearLayoutManager(this)
        boardAdapter = BoardAdapter()
        binding.recyclerviewBoard.adapter = boardAdapter

        viewModel.getMyRecruitment(type = "owned", listPage)

        binding.recyclerviewBoard.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 스크롤이 끝에 도달했는지 확인
                if (!binding.recyclerviewBoard.canScrollVertically(1)) {
                    listPage++
                    viewModel.getMyRecruitment(type = "owned", listPage)
                }
            }
        })

        observeData()
    }

    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(this@MyBoardActivity) {
                showToast(it)
            }

            boardList.observe(this@MyBoardActivity) {
                for(document in it) {
                    boardArrayList.add(document)
                }
                boardAdapter.addItems(boardArrayList)
            }

            val dialog = LoadingDialog(this@MyBoardActivity)
            progressVisible.observe(this@MyBoardActivity) {
                if (progressVisible.value!!) {
                    dialog.show()
                }
                else if (!progressVisible.value!!) {
                    dialog.dismiss()
                }
            }
        }
    }

    @SuppressLint("ShowToast")
    private fun showToast(msg: String) {
        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        } else toast?.setText(msg)
        toast?.show()
    }
}