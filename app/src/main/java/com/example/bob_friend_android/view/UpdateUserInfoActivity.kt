package com.example.bob_friend_android.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.ActivityUpdateUserInfoBinding
import com.example.bob_friend_android.viewmodel.UserViewModel
import kotlinx.android.synthetic.main.activity_join.*

class UpdateUserInfoActivity: AppCompatActivity() {

    private lateinit var binding: ActivityUpdateUserInfoBinding
    private lateinit var viewModel: UserViewModel
    var toast: Toast? = null

    private var nickname: String? = null
    private var password: String? = null
    private var passwordCheck: String? = null
    private var dateBirth: String? = null
    private var gender : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_user_info)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        binding.lifecycleOwner = this
        binding.list = viewModel

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.toolbar.title = "회원정보수정"

        observeData()

        binding.updateGenderGroup.setOnCheckedChangeListener { group, checkedId ->
            gender = when(checkedId) {
                R.id.male -> "MALE"
                R.id.female -> "FEMALE"
                R.id.third_gender -> "NONE"
                else -> ""
            }
        }

        var nicknameCheck = false
        binding.nicknameCheck.setOnClickListener {
            nickname = binding.editTextTextNickname.text.toString().trim()
            viewModel.checkUserNickname(nickname!!)
            nicknameCheck = true
        }

        binding.updateUserInfoBtn.setOnClickListener {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("회원정보수정")
                builder.setMessage("이렇게 정보 변경을 진행할까요?")

                if(binding.editTextTextNickname.text.isNotBlank()){
                        nickname = binding.editTextTextNickname.text.toString().trim()
                }
                if (binding.editTextTextPassword.text.isNotBlank()){
                    password = binding.editTextTextPassword.text.toString().trim()
                }
                if (binding.editTextTextPasswordCheck.text.isNotBlank()){
                    passwordCheck = binding.editTextTextPasswordCheck.text.toString().trim()
                }
                if (binding.editTextTextBirth.text.isNotBlank()){
                    dateBirth = binding.editTextTextBirth.text.toString().trim()
                }

                builder.setPositiveButton("예") { dialog, which ->
                        if(viewModel.validationUpdate(
                                nicknameCheck = nicknameCheck, nickname = nickname,
                                password = password, gender = gender,
                                passwordCheck = passwordCheck, dateBirth = dateBirth)){
                            viewModel.updateUser(agree = null, email = null, nickname = nickname,
                                password = password, sex = gender, birth = dateBirth
                            )
                            finish()
                        }
                }
                builder.setNegativeButton("아니오") { dialog, which ->
                    return@setNegativeButton
                }
                builder.show()
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
            errorMsg.observe(this@UpdateUserInfoActivity) {
                showToast(it)
            }

            userInfo.observe(this@UpdateUserInfoActivity, { user ->
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