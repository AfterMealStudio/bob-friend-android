package com.example.bob_friend_android.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.R
import com.example.bob_friend_android.adapter.SearchAdapter
import com.example.bob_friend_android.databinding.ActivityLocationSearchBinding
import com.example.bob_friend_android.model.SearchLocation
import com.example.bob_friend_android.viewmodel.ListViewModel

class LocationSearchActivity: AppCompatActivity() {

    private lateinit var binding: ActivityLocationSearchBinding
    private lateinit var viewModel: ListViewModel

    private val listItems = arrayListOf<SearchLocation>()
    private val searchAdapter = SearchAdapter(listItems)
    private var keyword = ""
    private var listPage = 0

    var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_location_search)
        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)
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
            if(keyword!="") {
                viewModel.searchKeywordMap(keyword)
            }
        }

        searchAdapter.setItemClickListener(object: SearchAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val intent = Intent().apply {
                    putExtra("location", listItems[position].address)
                    putExtra("name", listItems[position].name)
                    putExtra("x", listItems[position].x)
                    putExtra("y", listItems[position].y)
                }
                setResult(RESULT_OK, intent)
                if(!isFinishing) finish()
            }
        })

        observeData()
    }


    @SuppressLint("ShowToast")
    private fun showToast(msg: String) {
        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        } else toast?.setText(msg)
        toast?.show()
    }


    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(this@LocationSearchActivity) {
                showToast(it)
            }

            searchKeyword.observe(this@LocationSearchActivity) {
                var count = 0
                listItems.clear()
                for(document in it.documents) {
                    val searchLocation = SearchLocation(document.place_name, document.road_address_name, document.address_name, document.x.toDouble(), document.y.toDouble())
                    listItems.add(searchLocation)
                    count += 1
                }
                searchAdapter.addItems(listItems)

//                if (count >= 20){
//                    binding.searchRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener(){
//                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
//                            super.onScrolled(recyclerView, dx, dy)
//                            // 스크롤이 끝에 도달했는지 확인
//                            if (!binding.searchRecyclerview.canScrollVertically(1)) {
//                                listPage++
//                                viewModel.setList(listPage)
//                            }
//                        }
//                    })
//                }
            }
        }
    }
}