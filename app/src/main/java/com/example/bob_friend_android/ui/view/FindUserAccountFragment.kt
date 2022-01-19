package com.example.bob_friend_android.ui.view

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import com.example.bob_friend_android.R
import com.example.bob_friend_android.ui.view.base.BaseFragment
import com.example.bob_friend_android.databinding.FragmentFindUserAccountBinding
import com.example.bob_friend_android.ui.viewmodel.UserViewModel

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