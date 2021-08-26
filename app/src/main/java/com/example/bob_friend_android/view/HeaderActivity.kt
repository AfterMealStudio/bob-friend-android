package com.example.bob_friend_android.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.ActivityDrawerHeaderBinding
import com.example.bob_friend_android.viewmodel.HeaderViewModel

class HeaderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDrawerHeaderBinding
    private lateinit var viewModel: HeaderViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_drawer_header)
        viewModel = ViewModelProvider(this).get(HeaderViewModel::class.java)
        binding.lifecycleOwner = this
        binding.header = viewModel

    }
}