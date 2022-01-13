package com.example.bob_friend_android.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bob_friend_android.R
import com.example.bob_friend_android.ui.adapter.SearchAdapter
import com.example.bob_friend_android.databinding.FragmentSearchLocationBinding
import com.example.bob_friend_android.model.SearchLocation
import com.example.bob_friend_android.viewmodel.ListViewModel

class SearchLocationFragment: Fragment() {

    private lateinit var binding: FragmentSearchLocationBinding
    private lateinit var viewModel: ListViewModel

    private val listItems = arrayListOf<SearchLocation>()
    private val searchAdapter = SearchAdapter(listItems)
    private var keyword = ""
    private var listPage = 0

    var toast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search_location, container, false)
        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        binding.lifecycleOwner = this
        binding.boardsearch = viewModel


        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbarBoard)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        binding.searchRecyclerview.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.searchRecyclerview.adapter = searchAdapter

//        binding.searchBackBtn.setOnClickListener {
//            onBackPressed()
//        }

        binding.locationEditTextSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    keyword = binding.locationEditTextSearch.text.toString()
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
                val intent = Intent().apply {
                    putExtra("location", listItems[position].address)
                    putExtra("name", listItems[position].name)
                    putExtra("longitude", listItems[position].x)
                    putExtra("latitude", listItems[position].y)
                }
//                setResult(RESULT_OK, intent)
//                if(!isFinishing) finish()
            }
        })

        observeData()

        return binding.root
    }


    @SuppressLint("ShowToast")
    private fun showToast(msg: String) {
        if (toast == null) {
//            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        } else toast?.setText(msg)
        toast?.show()
    }


    fun hideKeyboard(){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.locationEditTextSearch.windowToken, 0)
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