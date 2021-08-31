package com.example.bob_friend_android.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.KeyboardVisibilityUtils
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.ActivityJoinBinding
import com.example.bob_friend_android.viewmodel.JoinViewModel
import java.time.LocalDate
import kotlin.properties.Delegates

class JoinActivity : AppCompatActivity() {

    val TAG = "JoinActivity"

    private lateinit var binding: ActivityJoinBinding
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils
    private lateinit var viewModel : JoinViewModel
    private lateinit var gender : String
    var agree by Delegates.notNull<Boolean>()

    private lateinit var userId: String
    private lateinit var nickname: String
    private lateinit var password: String
    private lateinit var passwordCheck: String
    private lateinit var email: String
    private lateinit var dateBirth: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_join)
        viewModel = ViewModelProvider(this).get(JoinViewModel::class.java)
        binding.join = this
        binding.lifecycleOwner = this

        gender = ""
        binding.registerGenderGroup.setOnCheckedChangeListener { group, checkedId ->
            gender = when(checkedId) {
                R.id.male -> "MALE"
                R.id.female -> "FEMALE"
                R.id.third_gender -> "THIRD"
                else -> ""
            }
        }

        val checkListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                when(buttonView.id) {
                    R.id.agree_all -> {
                                        binding.agree1.isChecked = true
                                        binding.agree2.isChecked = true
                                        binding.agree3.isChecked = true
                    }
                    R.id.agree1 -> if(binding.agree1.isChecked && binding.agree2.isChecked && binding.agree3.isChecked) binding.agreeAll.isChecked = true
                    R.id.agree2 -> if(binding.agree1.isChecked && binding.agree2.isChecked && binding.agree3.isChecked) binding.agreeAll.isChecked = true
                    R.id.agree3 -> if(binding.agree1.isChecked && binding.agree2.isChecked && binding.agree3.isChecked) binding.agreeAll.isChecked = true
                }
            }

            else {
                when(buttonView.id) {
                    R.id.agree1 -> binding.agreeAll.isChecked = false
                    R.id.agree2 -> binding.agreeAll.isChecked = false
                    R.id.agree3 -> binding.agreeAll.isChecked = false
                }
            }
        }

        binding.agreeAll.setOnCheckedChangeListener(checkListener)
        binding.agree1.setOnCheckedChangeListener(checkListener)
        binding.agree2.setOnCheckedChangeListener(checkListener)
        binding.agree3.setOnCheckedChangeListener(checkListener)

        agree = binding.agree3.isChecked

        binding.joinBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("회원가입")
            builder.setMessage("이렇게 회원가입을 진행할까요?")

            userId = binding.editTextId.text.toString().trim()
            nickname = binding.editTextUsername.text.toString().trim()
            password = binding.editTextPassword.text.toString().trim()
            passwordCheck = binding.editTextPasswordCheck.text.toString().trim()
            email = binding.editTextEmail.text.toString().trim()
            dateBirth = binding.editTextDateBirth.text.toString().trim()

            builder.setPositiveButton("예") { dialog, which ->
                viewModel.join(userId, password, passwordCheck, nickname, email, dateBirth, gender, agree, this)
            }
            builder.setNegativeButton("아니오") { dialog, which ->
                return@setNegativeButton
            }
            builder.show()
        }

        binding.idCheck.setOnClickListener {
            userId = binding.editTextId.text.toString().trim()
            Log.d(TAG, "userId: $userId")
            viewModel.checkUserId(userId, this)
        }

        binding.emailCheck.setOnClickListener {
            email = binding.editTextEmail.text.toString().trim()
            Log.d(TAG, "email: $email")
            viewModel.checkUserEmail(email, this)
        }

        keyboardVisibilityUtils = KeyboardVisibilityUtils(window,
            onShowKeyboard = { keyboardHeight ->
                binding.joinScroll.run {
                    smoothScrollTo(scrollX, scrollY + keyboardHeight)
                }
            })

    }
}