package com.example.bob_friend_android.view

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.ActivityWithdrawalBinding
import com.example.bob_friend_android.viewmodel.JoinViewModel
import kotlin.properties.Delegates

class DeleteUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWithdrawalBinding
    private lateinit var viewModel: JoinViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_withdrawal)
        viewModel = ViewModelProvider(this).get(JoinViewModel::class.java)
        binding.lifecycleOwner = this
        binding.list = viewModel

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.title = "회원탈퇴"

        val userId = App.prefs.getInt("id",2019108267)

        binding.deleteUserBtn.setOnClickListener {
            viewModel.deleteUser(binding.editTextTextPassword.text.toString(), userId)
        }
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