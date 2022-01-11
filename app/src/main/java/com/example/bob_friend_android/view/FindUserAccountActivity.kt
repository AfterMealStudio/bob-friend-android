package com.example.bob_friend_android.view

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.ActivityFindUserAccountBinding
import com.example.bob_friend_android.viewmodel.UserViewModel

class FindUserAccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFindUserAccountBinding
    private lateinit var viewModel : UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_find_user_account)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        binding.user= this
        binding.lifecycleOwner = this

        binding.emailCheck.setOnClickListener {

        }

        binding.changePassword.setOnClickListener {

        }

        binding.findLayout.setOnClickListener {
            hideKeyboard()
        }
    }

    private fun hideKeyboard(){
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editTextEmail.windowToken, 0)
        imm.hideSoftInputFromWindow(binding.editTextEmail2.windowToken, 0)
        imm.hideSoftInputFromWindow(binding.editTextDateBirth.windowToken, 0)
    }
}