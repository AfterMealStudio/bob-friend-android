package com.example.bob_friend_android.ui.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.FragmentBoardBinding
import com.example.bob_friend_android.ui.view.base.BaseFragment
import com.example.bob_friend_android.databinding.FragmentIntroduceTeamBinding
import com.example.bob_friend_android.ui.viewmodel.ListViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroduceTeamFragment : BaseFragment<FragmentIntroduceTeamBinding>(
    R.layout.fragment_introduce_team
) {
    private val viewModel by activityViewModels<ListViewModel>()

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentIntroduceTeamBinding {
        return FragmentIntroduceTeamBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun init() {
        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(requireDataBinding().toolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        requireDataBinding().toolbar.title = "개발자 정보"
    }
}