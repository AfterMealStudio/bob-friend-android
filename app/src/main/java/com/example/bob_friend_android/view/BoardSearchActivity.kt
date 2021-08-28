package com.example.bob_friend_android.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bob_friend_android.R
import com.example.bob_friend_android.adapter.SearchAdapter
import com.example.bob_friend_android.databinding.ActivityBoardSearchBinding
import com.example.bob_friend_android.model.SearchLocation
import com.example.bob_friend_android.viewmodel.MainViewModel

class BoardSearchActivity: AppCompatActivity() {

    private lateinit var binding: ActivityBoardSearchBinding
    private lateinit var viewModel: MainViewModel

    private val listItems = arrayListOf<SearchLocation>()
    private val searchAdapter = SearchAdapter(listItems)
    private var pageNumber = 1
    private var keyword = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_search)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding.lifecycleOwner = this
        binding.boardsearch = viewModel

        setSupportActionBar(binding.toolbarBoard)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.searchRecyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.searchRecyclerview.adapter = searchAdapter

        binding.searchBackBtn.setOnClickListener {
            onBackPressed()
        }

        binding.searchBtn.setOnClickListener {
            keyword = binding.editTextSearchLocation.text.toString()
            pageNumber = 1
            if(keyword!="") {
                viewModel.searchKeyword(keyword, searchAdapter,this)
            }
        }

        searchAdapter.setItemClickListener(object: SearchAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val intent = Intent(this@BoardSearchActivity, CreateBoardActivity::class.java).apply {
                    putExtra("location", listItems[position].address)
                    putExtra("name", listItems[position].name)
                    putExtra("x", listItems[position].x)
                    putExtra("y", listItems[position].y)
                }
                setResult(RESULT_OK, intent)
                if(!isFinishing) finish()
            }
        })
    }
}