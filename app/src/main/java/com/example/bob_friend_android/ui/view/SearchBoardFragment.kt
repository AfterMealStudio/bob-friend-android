package com.example.bob_friend_android.ui.view

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.R
import com.example.bob_friend_android.ui.view.base.BaseFragment
import com.example.bob_friend_android.ui.adapter.BoardAdapter
import com.example.bob_friend_android.databinding.FragmentSearchBoardBinding
import com.example.bob_friend_android.data.entity.Board
import com.example.bob_friend_android.databinding.FragmentBoardBinding
import com.example.bob_friend_android.ui.viewmodel.ListViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class SearchBoardFragment : BaseFragment<FragmentSearchBoardBinding>(
    R.layout.fragment_search_board
) {
    private val viewModel by activityViewModels<ListViewModel>()

    private val boardItems = arrayListOf<Board>()   // 리사이클러 뷰 아이템
    private val searchAdapter = BoardAdapter()    // 리사이클러 뷰 어댑터
    private var keyword = ""        // 검색 키워드
    private var listPage = 0 // 현재 페이지

    var category = "all"
    var start: String? = null
    var end : String? = null

    var startDate = ""
    var endDate = ""
    var startTime : String? = null
    var endTime : String? = null

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentSearchBoardBinding {
        return FragmentSearchBoardBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun init() {
        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(requireDataBinding().toolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        requireDataBinding().rvSearch.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        requireDataBinding().rvSearch.adapter = searchAdapter

        requireDataBinding().btnSearch.setOnClickListener {
            keyword = requireDataBinding().etvSearch.text.toString()
            searchList(keyword)
            hideKeyboard()
        }

        requireDataBinding().etvSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    keyword = requireDataBinding().etvSearch.text.toString()
                    searchList(keyword)
                    hideKeyboard()
                    return true
                }
                return false
            }
        })

        requireDataBinding().rvSearch.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 스크롤이 끝에 도달했는지 확인
                if (!requireDataBinding().rvSearch.canScrollVertically(1)) {
                    listPage++
                    viewModel.searchAppointmentList(page = listPage, keyword = keyword, category = category, start = start, end = end)
                }
            }
        })

        requireDataBinding().cbTimeLimit.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                showDateRangePicker()
                requireDataBinding().layoutTime.visibility = View.VISIBLE
            }
            else {
                requireDataBinding().layoutTime.visibility = View.GONE
            }
        }

        requireDataBinding().rgSearch.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rb_all -> {
                    requireDataBinding().rbAll.setTextColor(Color.parseColor("#FFFFFF"))
                    requireDataBinding().rbTitle.setTextColor(Color.parseColor("#000000"))
                    requireDataBinding().rbContent.setTextColor(Color.parseColor("#000000"))
                    requireDataBinding().rbPlace.setTextColor(Color.parseColor("#000000"))
                    category = "all"

                }
                R.id.rb_title -> {
                    requireDataBinding().rbAll.setTextColor(Color.parseColor("#000000"))
                    requireDataBinding().rbTitle.setTextColor(Color.parseColor("#FFFFFF"))
                    requireDataBinding().rbContent.setTextColor(Color.parseColor("#000000"))
                    requireDataBinding().rbPlace.setTextColor(Color.parseColor("#000000"))
                    category = "title"
                }
                R.id.rb_content -> {
                    requireDataBinding().rbAll.setTextColor(Color.parseColor("#000000"))
                    requireDataBinding().rbTitle.setTextColor(Color.parseColor("#000000"))
                    requireDataBinding().rbContent.setTextColor(Color.parseColor("#FFFFFF"))
                    requireDataBinding().rbPlace.setTextColor(Color.parseColor("#000000"))
                    category = "content"
                }
                R.id.rb_place -> {
                    requireDataBinding().rbAll.setTextColor(Color.parseColor("#000000"))
                    requireDataBinding().rbTitle.setTextColor(Color.parseColor("#000000"))
                    requireDataBinding().rbContent.setTextColor(Color.parseColor("#000000"))
                    requireDataBinding().rbPlace.setTextColor(Color.parseColor("#FFFFFF"))
                    category = "place"
                }
            }
        }

        requireDataBinding().btnSettingOnOff.setOnClickListener {
            if (requireDataBinding().layoutSetting.visibility == View.GONE){
                requireDataBinding().layoutSetting.visibility = View.VISIBLE
                requireDataBinding().btnSettingOnOff.setImageResource(R.drawable.up_arrow)
            }
            else if(requireDataBinding().layoutSetting.visibility == View.VISIBLE) {
                requireDataBinding().layoutSetting.visibility = View.GONE
                hideKeyboard()
                requireDataBinding().btnSettingOnOff.setImageResource(R.drawable.down_arrow)
            }
        }

        requireDataBinding().layoutSetting.setOnClickListener {
            hideKeyboard()
        }

        requireDataBinding().layoutSearch.setOnClickListener {
            hideKeyboard()
        }

        requireDataBinding().btnSearchReset.setOnClickListener {
            requireDataBinding().rgSearch.check(requireDataBinding().rbAll.id)
            requireDataBinding().rbAll.setTextColor(Color.parseColor("#FFFFFF"))
            requireDataBinding().rbTitle.setTextColor(Color.parseColor("#000000"))
            requireDataBinding().rbContent.setTextColor(Color.parseColor("#000000"))
            requireDataBinding().rbPlace.setTextColor(Color.parseColor("#000000"))
            requireDataBinding().cbTimeLimit.isChecked = false
            requireDataBinding().cbCondition.isChecked = false
        }

        searchAdapter.setOnItemClickListener(object : BoardAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: Board, pos: Int) {
                val action =
                    SearchBoardFragmentDirections.actionSearchBoardFragmentToSetBoardFragment(data.id.toString())
                findNavController().navigate(action)
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
            requireDataBinding().tvStartDate.text = startDate
            requireDataBinding().tvEndDate.text = endDate
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
                requireDataBinding().tvStartTime.text = startTime
                setCalenderTime(true)
            }
            else if (endFlag) {
                end = end + hour + minute
                endTime = "$hour:$minute"
                requireDataBinding().tvEndTime.text = endTime
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
                    requireDataBinding().cbTimeLimit.isChecked = false
                }
                else {
                    viewModel.searchAppointmentList(page = listPage, keyword = keyword, category = category, start = start, end = end)
                }
            }
            else {
                viewModel.searchAppointmentList(page = listPage, keyword = keyword, category = category, start = start, end = end)
            }
            requireDataBinding().layoutSetting.visibility = View.GONE
            requireDataBinding().btnSettingOnOff.setImageResource(R.drawable.down_arrow)
        }
        else {
            showToast("검색어를 입력해주세요.")
        }
    }


    private fun hideKeyboard(){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireDataBinding().etvSearch.windowToken, 0)
    }


    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(viewLifecycleOwner) {
                showToast(it)
            }

            appointmentList.observe(viewLifecycleOwner) {
                for(document in it.boardList) {
                    boardItems.add(document)
                }
                searchAdapter.addItems(boardItems)
            }
        }
    }
}