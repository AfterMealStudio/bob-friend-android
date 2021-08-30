package com.example.bob_friend_android.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.KeyboardVisibilityUtils
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.ActivityJoinBinding
import com.example.bob_friend_android.viewmodel.JoinViewModel
import java.time.LocalDate

class JoinActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJoinBinding
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils
    private lateinit var viewModel : JoinViewModel
    lateinit var gender : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_join)
        viewModel = ViewModelProvider(this).get(JoinViewModel::class.java)
        binding.join = this
        binding.lifecycleOwner = this


        binding.registerGenderGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId) {
                R.id.male -> gender = "MALE"
                R.id.female -> gender = "FEMALE"
                R.id.third_gender -> gender = "THIRD"
            }
        }

        binding.joinBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("회원가입")
            builder.setMessage("이렇게 회원가입을 진행할까요?")

            val userId = binding.editTextId.text.toString().trim()
            val nickname = binding.editTextUsername.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            val passwordCheck = binding.editTextPasswordCheck.text.toString().trim()
            val email = binding.editTextEmail.text.toString().trim()
            val dateBirth = binding.editTextDateBirth.text.toString().trim()

            val date = "${dateBirth.substring(0,4)}-${dateBirth.substring(4,6)}-${dateBirth.substring(6)}"

            builder.setPositiveButton("예") { dialog, which ->
                viewModel.join(userId, password, passwordCheck, nickname, email, date, gender, this)
            }
            builder.setNegativeButton("아니오") { dialog, which ->
                return@setNegativeButton
            }
            builder.show()
        }

        keyboardVisibilityUtils = KeyboardVisibilityUtils(window,
            onShowKeyboard = { keyboardHeight ->
                binding.joinScroll.run {
                    smoothScrollTo(scrollX, scrollY + keyboardHeight)
                }
            })

    }
}