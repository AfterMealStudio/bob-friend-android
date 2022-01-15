package com.example.bob_friend_android.view

import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.base.BaseFragment
import com.example.bob_friend_android.databinding.FragmentSetHomeBinding
import com.example.bob_friend_android.viewmodel.UserViewModel

class SetHomeFragment(override val viewModel: UserViewModel) : BaseFragment<FragmentSetHomeBinding, UserViewModel>(
    R.layout.fragment_set_home
) {
    override fun init() {
        viewModel.setUserInfo()
        observeData()
    }

    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(viewLifecycleOwner) {
                showToast(it)
            }

            userInfo.observe(viewLifecycleOwner, { user ->
                val editor = App.prefs.edit()
                editor.putInt("id", user.id)
                editor.putString("email", user.email)
                editor.putString("nickname", user.nickname)
                editor.putString("age", user.age)
                editor.putString("sex", user.sex)
                editor.putBoolean("agree", user.agree)
                editor.putFloat("rating", user.rating)
                editor.apply()
            })
        }
    }
}