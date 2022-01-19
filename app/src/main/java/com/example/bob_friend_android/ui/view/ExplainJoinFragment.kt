package com.example.bob_friend_android.ui.view

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.example.bob_friend_android.R
import com.example.bob_friend_android.ui.view.base.BaseFragment
import com.example.bob_friend_android.databinding.FragmentExplainJoinBinding
import com.example.bob_friend_android.ui.viewmodel.ListViewModel

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