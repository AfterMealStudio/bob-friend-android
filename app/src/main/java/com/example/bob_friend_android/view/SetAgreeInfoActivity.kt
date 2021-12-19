package com.example.bob_friend_android.view

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.ActivitySetAgreeInfoBinding
import com.example.bob_friend_android.viewmodel.ListViewModel

class SetAgreeInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetAgreeInfoBinding
    private lateinit var viewModel: ListViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_agree_info)
        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        binding.lifecycleOwner = this
        binding.list = viewModel

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.title = "정보 동의 설정"
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
}