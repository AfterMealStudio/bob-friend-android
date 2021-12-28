package com.example.bob_friend_android.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.ActivitySetAgreeInfoBinding
import com.example.bob_friend_android.viewmodel.UserViewModel

class AgreeInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySetAgreeInfoBinding
    private lateinit var viewModel: UserViewModel
    var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_agree_info)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        binding.lifecycleOwner = this
        binding.join = viewModel

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.title = "정보 동의 설정"
        binding.switch1.isChecked = App.prefs.getBoolean("agree", false)

        observeData()

        binding.switch1.setOnCheckedChangeListener { compoundButton, isChecked ->
            if (isChecked) {
                viewModel.updateUser(agree = true, email = null, nickname = null,
                    password = null, sex = null, birth = null
                )
            } else {
                viewModel.updateUser(agree = false, email = null, nickname = null,
                    password = null, sex = null, birth = null
                )
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


    @SuppressLint("ShowToast")
    private fun showToast(msg: String) {
        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        } else toast?.setText(msg)
        toast?.show()
    }

    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(this@AgreeInfoActivity) {
                showToast(it)
            }

            userInfo.observe(this@AgreeInfoActivity, { user ->
                val editor = App.prefs.edit()
                editor.putBoolean("agree", user.agree)
                editor.apply()
            })
        }
    }
}