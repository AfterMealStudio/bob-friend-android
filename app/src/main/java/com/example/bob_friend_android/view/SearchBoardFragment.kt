package com.example.bob_friend_android.view

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.R
import com.example.bob_friend_android.base.BaseFragment
import com.example.bob_friend_android.ui.adapter.BoardAdapter
import com.example.bob_friend_android.databinding.FragmentSearchBoardBinding
import com.example.bob_friend_android.model.Board
import com.example.bob_friend_android.viewmodel.ListViewModel
import com.example.bob_friend_android.viewmodel.UserViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*

class SearchBoardFragment : BaseFragment<FragmentSearchBoardBinding>(
    R.layout.fragment_search_board
) {
    private val viewModel by activityViewModels<ListViewModel>()

    private val boardItems = arrayListOf<Board>()   // 리사이클러 뷰 아이템
    private val searchAdapter = BoardAdapter()    // 리사이클러 뷰 어댑터
    private var keyword = ""        // 검색 키워드
    private var listPage = 0 // 현재 페이지

    var toast: Toast? = null

    var category = "all"
    var start: String? = null
    var end : String? = null

    var startDate = ""
    var endDate = ""
    var startTime : String? = null
    var endTime : String? = null


    override fun init() {
        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        binding.rvSearch.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvSearch.adapter = searchAdapter

        binding.btnSearch.setOnClickListener {
            keyword = binding.etvSearch.text.toString()
            searchList(keyword)
            hideKeyboard()
        }

        binding.etvSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    keyword = binding.etvSearch.text.toString()
                    searchList(keyword)
                    hideKeyboard()
                    return true
                }
                return false
            }
        })

        binding.rvSearch.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 스크롤이 끝에 도달했는지 확인
                if (!binding.rvSearch.canScrollVertically(1)) {
                    listPage++
                    viewModel.searchList(listPage = listPage, keyword = keyword, category = category, start = start, end = end)
                }
            }
        })

        binding.cbTimeLimit.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                showDateRangePicker()
                binding.layoutTime.visibility = View.VISIBLE
            }
            else {
                binding.layoutTime.visibility = View.GONE
            }
        }

        binding.rgSearch.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_all -> {
                    binding.rbAll.setTextColor(Color.parseColor("#FFFFFF"))
                    binding.rbTitle.setTextColor(Color.parseColor("#000000"))
                    binding.rbContent.setTextColor(Color.parseColor("#000000"))
                    binding.rbPlace.setTextColor(Color.parseColor("#000000"))
                    category = "all"

                }
                R.id.rb_title -> {
                    binding.rbAll.setTextColor(Color.parseColor("#000000"))
                    binding.rbTitle.setTextColor(Color.parseColor("#FFFFFF"))
                    binding.rbContent.setTextColor(Color.parseColor("#000000"))
                    binding.rbPlace.setTextColor(Color.parseColor("#000000"))
                    category = "title"
                }
                R.id.rb_content -> {
                    binding.rbAll.setTextColor(Color.parseColor("#000000"))
                    binding.rbTitle.setTextColor(Color.parseColor("#000000"))
                    binding.rbContent.setTextColor(Color.parseColor("#FFFFFF"))
                    binding.rbPlace.setTextColor(Color.parseColor("#000000"))
                    category = "content"
                }
                R.id.rb_place -> {
                    binding.rbAll.setTextColor(Color.parseColor("#000000"))
                    binding.rbTitle.setTextColor(Color.parseColor("#000000"))
                    binding.rbContent.setTextColor(Color.parseColor("#000000"))
                    binding.rbPlace.setTextColor(Color.parseColor("#FFFFFF"))
                    category = "place"
                }
            }
        }

        binding.btnSettingOnOff.setOnClickListener {
            if (binding.layoutSetting.visibility == View.GONE){
                binding.layoutSetting.visibility = View.VISIBLE
                binding.btnSettingOnOff.setImageResource(R.drawable.up_arrow)
            }
            else if(binding.layoutSetting.visibility == View.VISIBLE) {
                binding.layoutSetting.visibility = View.GONE
                hideKeyboard()
                binding.btnSettingOnOff.setImageResource(R.drawable.down_arrow)
            }
        }

        binding.layoutSetting.setOnClickListener {
            hideKeyboard()
        }

        binding.layoutSearch.setOnClickListener {
            hideKeyboard()
        }

        binding.btnSearchReset.setOnClickListener {
            binding.rgSearch.check(binding.rbAll.id)
            binding.rbAll.setTextColor(Color.parseColor("#FFFFFF"))
            binding.rbTitle.setTextColor(Color.parseColor("#000000"))
            binding.rbContent.setTextColor(Color.parseColor("#000000"))
            binding.rbPlace.setTextColor(Color.parseColor("#000000"))
            binding.cbTimeLimit.isChecked = false
            binding.cbCondition.isChecked = false
        }

        searchAdapter.setOnItemClickListener(object : BoardAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: Board, pos: Int) {
                goToNext(R.id.action_searchBoardFragment_to_setBoardFragment, boardId = data.id)
            }
        })

        observeData()
    }


    private fun showDateRangePicker(){
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        val title = resources.getString(R.string.search_time_picker)
        builder.setTitleText(title)

        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())

        builder.setCalendarConstraints(constraintsBuilder.build())

        val picker = builder.build()
        picker.show(requireActivity().supportFragmentManager, picker.toString())
        picker.addOnNegativeButtonClickListener{ picker.dismiss() }
        picker.addOnPositiveButtonClickListener {
            startDate = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(it.first)
            start = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(it.first)
            endDate = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(it.second)
            end = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(it.second)
            binding.tvStartDate.text = startDate
            binding.tvEndDate.text = endDate
            Log.d("test", "startDate: $start, endDate : $end")

            setCalenderTime(false)
        }
    }


    private fun setCalenderTime(endFlag: Boolean){
        val picker =
            MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(10)
                .build()
        picker.show(requireActivity().supportFragmentManager, "tag");

        picker.addOnPositiveButtonClickListener {
            var hour = picker.hour.toString()
            var minute = picker.minute.toString()

            if(hour.length != 2){
                hour = "0$hour"
            }

            if(minute.length != 2){
                minute = "0$minute"
            }

            if (!endFlag) {
                start = start + hour + minute
                startTime = "$hour:$minute"
                binding.tvStartTime.text = startTime
                setCalenderTime(true)
            }
            else if (endFlag) {
                end = end + hour + minute
                endTime = "$hour:$minute"
                binding.tvEndTime.text = endTime
            }
        }
    }


    private fun searchList(keyword: String) {
        boardItems.clear()
        listPage = 0
        if(keyword!="") {
            if (startTime!=null && endTime!=null) {
                val time1 = startTime!!.split(":")
                val time2 = endTime!!.split(":")
                val before = time1[0]+time1[1]
                val after = time2[0]+time2[1]

                if(startDate == endDate && before.toInt() > after.toInt()){
                    showToast("시간 형식이 잘못되었습니다.")
                    binding.cbTimeLimit.isChecked = false
                }
                else {
                    viewModel.searchList(listPage = listPage, keyword = keyword, category = category, start = start, end = end)
                }
            }
            else {
                viewModel.searchList(listPage = listPage, keyword = keyword, category = category, start = start, end = end)
            }
            binding.layoutSetting.visibility = View.GONE
            binding.btnSettingOnOff.setImageResource(R.drawable.down_arrow)
        }
        else {
            showToast("검색어를 입력해주세요.")
        }
    }


    private fun hideKeyboard(){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etvSearch.windowToken, 0)
    }


    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(viewLifecycleOwner) {
                showToast(it)
            }

            boardList.observe(viewLifecycleOwner) {
                for(document in it.boardList) {
                    boardItems.add(document)
                }
                searchAdapter.addItems(boardItems)
            }
        }
    }
}