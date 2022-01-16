package com.example.bob_friend_android.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.R
import com.example.bob_friend_android.base.BaseFragment
import com.example.bob_friend_android.databinding.FragmentExplainJoinBinding
import com.example.bob_friend_android.databinding.FragmentFindUserAccountBinding
import com.example.bob_friend_android.viewmodel.ListViewModel
import com.example.bob_friend_android.viewmodel.UserViewModel

class FindUserAccountFragment : BaseFragment<FragmentFindUserAccountBinding>(
    R.layout.fragment_find_user_account
) {
    private val viewModel by activityViewModels<UserViewModel>()

    override fun init() {
        binding.btnEmailCheck.setOnClickListener {

        }

        binding.btnChangePassword.setOnClickListener {

        }

        binding.layoutFind.setOnClickListener {
            hideKeyboard()
        }
    }
    private fun hideKeyboard(){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etvEmail.windowToken, 0)
        imm.hideSoftInputFromWindow(binding.etvEmail2.windowToken, 0)
        imm.hideSoftInputFromWindow(binding.etvDateBirth.windowToken, 0)
    }
}