package com.example.bob_friend_android.ui.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.FragmentBoardBinding
import com.example.bob_friend_android.ui.view.base.BaseFragment
import com.example.bob_friend_android.databinding.FragmentExplainJoinBinding
import com.example.bob_friend_android.ui.viewmodel.ListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExplainJoinFragment : BaseFragment<FragmentExplainJoinBinding>(
    R.layout.fragment_explain_join
) {
    private val viewModel by activityViewModels<ListViewModel>()

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentExplainJoinBinding {
        return FragmentExplainJoinBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun init() {
        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(requireDataBinding().toolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        requireDataBinding().toolbar.title = "로그인 방법"

        requireDataBinding().btnOk.setOnClickListener {
            goToNext(R.id.action_explainJoinFragment_to_loginFragment)
        }
    }
}