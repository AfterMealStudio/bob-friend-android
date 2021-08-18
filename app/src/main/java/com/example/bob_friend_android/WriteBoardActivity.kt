package com.example.bob_friend_android


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.bob_friend_android.databinding.ActivityWriteBoardBinding

class WriteBoardActivity : AppCompatActivity() {
    private lateinit var binding : ActivityWriteBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWriteBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}