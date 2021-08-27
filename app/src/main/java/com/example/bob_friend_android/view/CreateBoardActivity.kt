package com.example.bob_friend_android.view


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.ActivityCreateBoardBinding
import com.example.bob_friend_android.viewmodel.CreateBoardViewModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class CreateBoardActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCreateBoardBinding
    private lateinit var viewModel : CreateBoardViewModel
    var isToday : Boolean = true
    private val format : DateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.KOREAN)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_create_board)
        viewModel = ViewModelProvider(this).get(CreateBoardViewModel::class.java)
        binding.create = this
        binding.lifecycleOwner = this

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.backBtn.setOnClickListener {
            finish()
        }

        binding.ageDetail.visibility = View.GONE
        binding.ageGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.ageButton2 -> {
                    binding.ageDetail.visibility = View.VISIBLE
                }
                R.id.ageButton1 -> binding.ageDetail.visibility = View.GONE
            }
        }

        binding.calendarView.visibility = View.GONE
        binding.dayGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.todayBtn -> binding.calendarView.visibility = View.GONE
                R.id.notTodayBtn -> binding.calendarView.visibility = View.VISIBLE
            }
        }

        binding.writeOkBtn.setOnClickListener {
            val title = binding.editCreateTitle.text.toString().trim()
            val boardContent = binding.editCreateContent.text.toString().trim()
            viewModel.CreateBoard(title, boardContent, this)
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}