package com.example.bob_friend_android.view

import androidx.appcompat.app.AppCompatActivity
import com.example.bob_friend_android.R
import com.example.bob_friend_android.base.BaseFragment
import com.example.bob_friend_android.databinding.FragmentIntroduceTeamBinding
import com.example.bob_friend_android.viewmodel.ListViewModel

class IntroduceTeamFragment(override val viewModel: ListViewModel) : BaseFragment<FragmentIntroduceTeamBinding, ListViewModel>(
    R.layout.fragment_introduce_team
) {
    override fun init() {
        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding.toolbar.title = "개발자 정보"
    }
}