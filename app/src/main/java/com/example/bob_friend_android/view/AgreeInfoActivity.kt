package com.example.bob_friend_android.view

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.ActivitySetAgreeInfoBinding
import com.example.bob_friend_android.viewmodel.JoinViewModel
import com.example.bob_friend_android.viewmodel.ListViewModel

class AgreeInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetAgreeInfoBinding
    private lateinit var viewModel: JoinViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_agree_info)
        viewModel = ViewModelProvider(this).get(JoinViewModel::class.java)
        binding.lifecycleOwner = this
        binding.join = viewModel

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.title = "정보 동의 설정"
        binding.switch1.isChecked = App.prefs.getBoolean("agree", false)

        binding.switch1.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                viewModel.updateUser(agree = true, email = null, nickname = null,
                    password = null, sex = null, birth = null
                )
                val editor = App.prefs.edit()
                editor.putBoolean("agree", true)
                editor.apply()
            } else {
                viewModel.updateUser(agree = false, email = null, nickname = null,
                    password = null, sex = null, birth = null
                )
                val editor = App.prefs.edit()
                editor.putBoolean("agree", false)
                editor.apply()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}