package com.example.bob_friend_android.view

import android.content.Context
import android.content.Intent
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bob_friend_android.R
import com.example.bob_friend_android.base.BaseFragment
import com.example.bob_friend_android.ui.adapter.SearchAdapter
import com.example.bob_friend_android.databinding.FragmentSearchLocationBinding
import com.example.bob_friend_android.model.SearchLocation
import com.example.bob_friend_android.viewmodel.ListViewModel

class SearchLocationFragment(override val viewModel: ListViewModel) : BaseFragment<FragmentSearchLocationBinding, ListViewModel>(
    R.layout.fragment_search_location
) {

    private val listItems = arrayListOf<SearchLocation>()
    private val searchAdapter = SearchAdapter(listItems)
    private var keyword = ""
    private var listPage = 0

    override fun init() {
        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.tbSearch)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        binding.rvSearch.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvSearch.adapter = searchAdapter

//        binding.searchBackBtn.setOnClickListener {
//            onBackPressed()
//        }

        binding.etvSearchLocation.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    keyword = binding.etvSearchLocation.text.toString()
                    if(keyword!="") {
                        viewModel.searchKeywordMap(keyword)
                    }
                    hideKeyboard()
                    return true
                }
                return false
            }
        })


        searchAdapter.setItemClickListener(object: SearchAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                goToNext(R.id.action_searchBoardFragment_to_setBoardFragment, location = listItems[position])
            }
        })

        observeData()
    }


    fun hideKeyboard(){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etvSearchLocation.windowToken, 0)
    }


    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(viewLifecycleOwner) {
                showToast(it)
            }

            searchKeyword.observe(viewLifecycleOwner) {
                var count = 0
                listItems.clear()
                for(document in it.documents) {
                    val searchLocation = SearchLocation(document.place_name, document.road_address_name, document.address_name, document.x.toDouble(), document.y.toDouble())
                    listItems.add(searchLocation)
                    count += 1
                }
                searchAdapter.addItems(listItems)
            }
        }
    }
}