package com.example.bob_friend_android.view

import androidx.appcompat.app.AppCompatActivity
import com.example.bob_friend_android.R
import com.example.bob_friend_android.base.BaseFragment
import com.example.bob_friend_android.databinding.FragmentExplainJoinBinding
import com.example.bob_friend_android.viewmodel.ListViewModel

class ExplainJoinFragment(override val viewModel: ListViewModel) : BaseFragment<FragmentExplainJoinBinding, ListViewModel>(
    R.layout.fragment_explain_join
) {
    override fun init() {
        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding.toolbar.title = "로그인 방법"

        binding.btnOk.setOnClickListener {
            goToNext(R.id.action_explainJoinFragment_to_loginFragment)
        }
    }
}