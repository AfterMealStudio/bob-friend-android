package com.example.bob_friend_android.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.ActivityWithdrawalBinding
import com.example.bob_friend_android.viewmodel.UserViewModel
import kotlin.properties.Delegates

class DeleteUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityWithdrawalBinding
    private lateinit var viewModel: UserViewModel
    private lateinit var token: String
    var toast:Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_withdrawal)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        binding.lifecycleOwner = this
        binding.list = viewModel

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.title = "회원탈퇴"

        observeData()

        token = App.prefs.getString("token", "").toString()

        binding.deleteUserBtn.setOnClickListener {
            viewModel.deleteUser(token, binding.editTextTextPassword.text.toString())
            val editor = App.prefs.edit()
            editor.clear()
            editor.apply()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
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

    @SuppressLint("ShowToast")
    private fun showToast(msg: String) {
        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        } else toast?.setText(msg)
        toast?.show()
    }

    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(this@DeleteUserActivity) {
                showToast(it)
            }
        }
    }
}