package com.example.bob_friend_android.view

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.example.bob_friend_android.R
import com.example.bob_friend_android.base.BaseFragment
import com.example.bob_friend_android.databinding.FragmentExplainJoinBinding
import com.example.bob_friend_android.viewmodel.ListViewModel
import com.example.bob_friend_android.viewmodel.UserViewModel

class ExplainJoinFragment : BaseFragment<FragmentExplainJoinBinding>(
    R.layout.fragment_explain_join
) {
    private val viewModel by activityViewModels<ListViewModel>()

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