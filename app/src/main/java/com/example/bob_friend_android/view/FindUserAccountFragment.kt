package com.example.bob_friend_android.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.FragmentFindUserAccountBinding
import com.example.bob_friend_android.viewmodel.UserViewModel

class FindUserAccountFragment : Fragment() {

    private lateinit var binding: FragmentFindUserAccountBinding
    private lateinit var viewModel : UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_find_user_account, container, false)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        binding.user= viewModel
        binding.lifecycleOwner = this

        binding.emailCheck.setOnClickListener {

        }

        binding.changePassword.setOnClickListener {

        }

        binding.findLayout.setOnClickListener {
            hideKeyboard()
        }

        return binding.root
    }

    private fun hideKeyboard(){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editTextEmail.windowToken, 0)
        imm.hideSoftInputFromWindow(binding.editTextEmail2.windowToken, 0)
        imm.hideSoftInputFromWindow(binding.editTextDateBirth.windowToken, 0)
    }
}