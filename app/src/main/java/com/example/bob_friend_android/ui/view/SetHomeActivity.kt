package com.example.bob_friend_android.ui.view

import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.ui.view.base.BaseActivity
import com.example.bob_friend_android.databinding.ActivitySetHomeBinding
import com.example.bob_friend_android.ui.viewmodel.UserViewModel

class SetHomeActivity : BaseActivity<ActivitySetHomeBinding>(
    R.layout.activity_set_home
) {
    lateinit var navController: NavController
    private lateinit var viewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment
        navController = navHostFragment.findNavController()
        binding.bnvHome.setupWithNavController(navController)

        viewModel.setUserInfo()

        observeData()
    }

    private fun observeData() {
        with(viewModel) {
            userInfo.observe(this@SetHomeActivity, { user ->
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