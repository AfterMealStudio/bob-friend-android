package com.example.bob_friend_android.view

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import com.example.bob_friend_android.R
import com.example.bob_friend_android.base.BaseFragment
import com.example.bob_friend_android.databinding.FragmentIntroduceTeamBinding
import com.example.bob_friend_android.viewmodel.ListViewModel
import com.example.bob_friend_android.viewmodel.UserViewModel

class IntroduceTeamFragment : BaseFragment<FragmentIntroduceTeamBinding>(
    R.layout.fragment_introduce_team
) {
    private val viewModel by activityViewModels<ListViewModel>()

    override fun init() {
        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding.toolbar.title = "개발자 정보"
    }
}