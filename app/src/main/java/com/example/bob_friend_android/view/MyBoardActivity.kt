package com.example.bob_friend_android.view

import android.annotation.SuppressLint
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
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

    private var type : String = ""
    private var sort : String = ""

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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.myboardToolbar.navigationIcon?.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                colorFilter = BlendModeColorFilter(Color.WHITE, BlendMode.SRC_IN)
            }else{
                setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
            }
        }

        binding.recyclerviewBoard.layoutManager = LinearLayoutManager(this)
        boardAdapter = BoardAdapter()
        binding.recyclerviewBoard.adapter = boardAdapter

        if(intent.hasExtra("type")){
            type = intent.getStringExtra("type").toString()
            sort = intent.getStringExtra("sort").toString()

            viewModel.getMyRecruitment(type, listPage, sort)

            binding.recyclerviewBoard.addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    // 스크롤이 끝에 도달했는지 확인
                    if (!binding.recyclerviewBoard.canScrollVertically(1)) {
                        listPage++
                        viewModel.getMyRecruitment(type, listPage, sort)
                    }
                }
            })
        }

        observeData()
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onContextItemSelected(item)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("ShowToast")
    private fun showToast(msg: String) {
        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        } else toast?.setText(msg)
        toast?.show()
    }
}