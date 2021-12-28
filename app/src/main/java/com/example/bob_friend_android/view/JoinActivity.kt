package com.example.bob_friend_android.view

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.KeyboardVisibilityUtils
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.ActivityJoinBinding
import com.example.bob_friend_android.viewmodel.UserViewModel
import kotlin.properties.Delegates

class JoinActivity : AppCompatActivity() {

    val TAG = "JoinActivity"

    private lateinit var binding: ActivityJoinBinding
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils
    private lateinit var viewModel : UserViewModel

    var agreeAll by Delegates.notNull<Boolean>() //동의하기
    var agree1 by Delegates.notNull<Boolean>()
    var agree2 by Delegates.notNull<Boolean>()
    var agreeChoice by Delegates.notNull<Boolean>()

    private lateinit var nickname: String //입력 정보등
    private lateinit var password: String
    private lateinit var passwordCheck: String
    private lateinit var email: String
    private lateinit var dateBirth: String
    private lateinit var gender : String

    var emailCheck = false
    var nicknameCheck = false

    var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_join)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
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


        binding.joinBtn.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("회원가입")
            builder.setMessage("이렇게 회원가입을 진행할까요?")

            nickname = binding.editTextNickname.text.toString().trim()
            password = binding.editTextPassword.text.toString().trim()
            passwordCheck = binding.editTextPasswordCheck.text.toString().trim()
            email = binding.editTextEmail.text.toString().trim()
            dateBirth = binding.editTextDateBirth.text.toString().trim()
            agreeAll = binding.agreeAll.isChecked
            agree1 = binding.agree1.isChecked
            agree2 = binding.agree2.isChecked
            agreeChoice = binding.agree3.isChecked

            builder.setPositiveButton("예") { dialog, which ->
                viewModel.joinUser(password, passwordCheck, nickname, email, dateBirth, gender, agree1, agree2, agreeChoice, nicknameCheck, emailCheck)
            }
            builder.setNegativeButton("아니오") { dialog, which ->
                return@setNegativeButton
            }
            builder.show()
        }

        binding.emailCheck.setOnClickListener {
            email = binding.editTextEmail.text.toString().trim()
            Log.d(TAG, "email: $email")
            viewModel.checkUserEmail(email)
            emailCheck = true
        }

        binding.nicknameCheck.setOnClickListener {
            nickname = binding.editTextNickname.text.toString().trim()
            Log.d(TAG, "nickname: $nickname")
            viewModel.checkUserNickname(nickname)
            nicknameCheck = true
        }

        keyboardVisibilityUtils = KeyboardVisibilityUtils(window,
            onShowKeyboard = { keyboardHeight ->
                binding.joinScroll.run {
                    smoothScrollTo(scrollX, scrollY + keyboardHeight)
                }
            })

        observeData()
    }


    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(this@JoinActivity) {
                showToast(it)
                if (it == "회원가입에 성공했습니다."){
                    val intent = Intent(this@JoinActivity, LoginActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            startActivity(intent)
                }
            }

            val dialog = LoadingDialog(this@JoinActivity)
            progressVisible.observe(this@JoinActivity) {
                if (progressVisible.value!!) {
                    dialog.show()
                }
                else if (!progressVisible.value!!) {
                    dialog.dismiss()
                }
            }
        }
    }


    @SuppressLint("ShowToast")
    private fun showToast(msg: String) {
        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        } else toast?.setText(msg)
        toast?.show()
    }
}