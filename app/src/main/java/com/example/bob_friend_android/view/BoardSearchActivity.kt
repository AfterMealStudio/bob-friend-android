package com.example.bob_friend_android.view

import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.R
import com.example.bob_friend_android.adapter.BoardAdapter
import com.example.bob_friend_android.adapter.SearchAdapter
import com.example.bob_friend_android.databinding.ActivityBoardSearchBinding
import com.example.bob_friend_android.databinding.ActivityLocationSearchBinding
import com.example.bob_friend_android.model.Board
import com.example.bob_friend_android.model.SearchLocation
import com.example.bob_friend_android.viewmodel.ListViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_KEYBOARD
import com.google.android.material.timepicker.TimeFormat
import kotlinx.android.synthetic.main.activity_board_search.*
import java.text.SimpleDateFormat
import java.util.*

class BoardSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBoardSearchBinding
    private lateinit var viewModel: ListViewModel

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_board_search)
        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        binding.lifecycleOwner = this
        binding.boardsearch = viewModel

        setSupportActionBar(binding.toolbarBoard)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.searchRecyclerview.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.searchRecyclerview.adapter = searchAdapter

        binding.searchBtn.setOnClickListener {
            keyword = binding.editTextSearchBoard.text.toString()
            searchList(keyword)
            hideKeyboard()
        }

        binding.editTextSearchBoard.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    keyword = binding.editTextSearchBoard.text.toString()
                    searchList(keyword)
                    hideKeyboard()
                    return true
                }
                return false
            }
        })

        binding.searchRecyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                // 스크롤이 끝에 도달했는지 확인
                if (!binding.searchRecyclerview.canScrollVertically(1)) {
                    listPage++
                    viewModel.searchList(listPage = listPage, keyword = keyword, category = category, start = start, end = end)
                }
            }
        })

        binding.searchCheckTime.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                showDateRangePicker()
                binding.searchTime1.visibility = View.VISIBLE
            }
            else {
                binding.searchTime1.visibility = View.GONE
            }
        }

        binding.searchRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radioButton_all -> {
                    binding.radioButtonAll.setTextColor(Color.parseColor("#FFFFFF"))
                    binding.radioButtonTitle.setTextColor(Color.parseColor("#000000"))
                    binding.radioButtonContent.setTextColor(Color.parseColor("#000000"))
                    binding.radioButtonPlace.setTextColor(Color.parseColor("#000000"))
                    category = "all"

                }
                R.id.radioButton_title -> {
                    binding.radioButtonAll.setTextColor(Color.parseColor("#000000"))
                    binding.radioButtonContent.setTextColor(Color.parseColor("#000000"))
                    binding.radioButtonPlace.setTextColor(Color.parseColor("#000000"))
                    binding.radioButtonTitle.setTextColor(Color.parseColor("#FFFFFF"))
                    category = "title"
                }
                R.id.radioButton_content -> {
                    binding.radioButtonAll.setTextColor(Color.parseColor("#000000"))
                    binding.radioButtonTitle.setTextColor(Color.parseColor("#000000"))
                    binding.radioButtonPlace.setTextColor(Color.parseColor("#000000"))
                    binding.radioButtonContent.setTextColor(Color.parseColor("#FFFFFF"))
                    category = "content"
                }
                R.id.radioButton_place -> {
                    binding.radioButtonAll.setTextColor(Color.parseColor("#000000"))
                    binding.radioButtonTitle.setTextColor(Color.parseColor("#000000"))
                    binding.radioButtonContent.setTextColor(Color.parseColor("#000000"))
                    binding.radioButtonPlace.setTextColor(Color.parseColor("#FFFFFF"))
                    category = "place"
                }
            }
        }

        binding.searchSettingOnOffBtn.setOnClickListener {
            if (binding.searchSettingView.visibility == View.GONE){
                binding.searchSettingView.visibility = View.VISIBLE
                binding.searchSettingOnOffBtn.setImageResource(R.drawable.up_arrow)
            }
            else if(binding.searchSettingView.visibility == View.VISIBLE) {
                binding.searchSettingView.visibility = View.GONE
                binding.searchSettingOnOffBtn.setImageResource(R.drawable.down_arrow)
            }
        }

        binding.searchSettingView.setOnClickListener {
            hideKeyboard()
        }

        binding.searchBackBtn.setOnClickListener {
            onBackPressed()
        }

        binding.searchResetBtn.setOnClickListener {
            binding.searchRadioGroup.check(binding.radioButtonAll.id)
            binding.radioButtonAll.setTextColor(Color.parseColor("#FFFFFF"))
            binding.radioButtonTitle.setTextColor(Color.parseColor("#000000"))
            binding.radioButtonContent.setTextColor(Color.parseColor("#000000"))
            binding.radioButtonPlace.setTextColor(Color.parseColor("#000000"))
            binding.searchCheckTime.isChecked = false
            binding.searchCheckCondition.isChecked = false
        }

        searchAdapter.setOnItemClickListener(object : BoardAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: Board, pos: Int) {
                val intent = Intent(this@BoardSearchActivity, DetailBoardActivity::class.java)
                intent.putExtra("boardId", data.id)
                intent.putExtra("userId", data.author!!.id)
//                getListResultLauncher.launch(intent)
                startActivity(intent)

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
        picker.show(supportFragmentManager, picker.toString())
        picker.addOnNegativeButtonClickListener{ picker.dismiss() }
        picker.addOnPositiveButtonClickListener {
            startDate = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(it.first)
            start = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(it.first)
            endDate = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault()).format(it.second)
            end = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(it.second)
            binding.startDate.text = startDate
            binding.endDate.text = endDate
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
        picker.show(supportFragmentManager, "tag");

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
                binding.startTime.text = startTime
                setCalenderTime(true)
            }
            else if (endFlag) {
                end = end + hour + minute
                endTime = "$hour:$minute"
                binding.endTime.text = endTime
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
                    binding.searchCheckTime.isChecked = false
                }
                else {
                    viewModel.searchList(listPage = listPage, keyword = keyword, category = category, start = start, end = end)
                }
            }
            else {
                viewModel.searchList(listPage = listPage, keyword = keyword, category = category, start = start, end = end)
            }
            binding.searchSettingView.visibility = View.GONE
            binding.searchSettingOnOffBtn.setImageResource(R.drawable.down_arrow)
        }
        else {
            showToast("검색어를 입력해주세요.")
        }
    }


    private fun hideKeyboard(){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editTextSearchBoard.windowToken, 0)
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
            errorMsg.observe(this@BoardSearchActivity) {
                showToast(it)
            }

            boardList.observe(this@BoardSearchActivity) {
                for(document in it.boardList) {
                    boardItems.add(document)
                }
                searchAdapter.addItems(boardItems)
            }
        }
    }
}