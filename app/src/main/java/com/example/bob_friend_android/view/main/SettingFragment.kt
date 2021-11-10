package com.example.bob_friend_android.view.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.FragmentSettingBinding
import com.example.bob_friend_android.view.LoginActivity
import com.example.bob_friend_android.viewmodel.LoginViewModel

class SettingFragment : Fragment() {
    private lateinit var binding: FragmentSettingBinding
    private lateinit var viewModel: LoginViewModel

    @SuppressLint("CommitPrefEdits")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_setting, container, false)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.lifecycleOwner = this
        binding.logout = viewModel

        binding.headerEmail.text = App.prefs.getString("email", "")
        binding.headerUsername.text = App.prefs.getString("nickname","")

        binding.logoutBtn.setOnClickListener {
            val editor = App.prefs.edit()
            editor.clear()
            editor.apply()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }

        return binding.root
    }
}