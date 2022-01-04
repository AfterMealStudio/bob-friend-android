package com.example.bob_friend_android.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.ActivityJoinBinding
import com.example.bob_friend_android.viewmodel.UserViewModel
import kotlin.properties.Delegates


class JoinActivity : AppCompatActivity() {

    val TAG = "JoinActivity"

    private lateinit var binding: ActivityJoinBinding
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
                    R.id.agree1 -> if (binding.agree1.isChecked && binding.agree2.isChecked && binding.agree3.isChecked) binding.agreeAll.isChecked =
                        true
                    R.id.agree2 -> if (binding.agree1.isChecked && binding.agree2.isChecked && binding.agree3.isChecked) binding.agreeAll.isChecked =
                        true
                    R.id.agree3 -> if (binding.agree1.isChecked && binding.agree2.isChecked && binding.agree3.isChecked) binding.agreeAll.isChecked =
                        true
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
                if (!validateEmail() || !validateNickname() || !validatePassword() || !validateDateBirth() || !validateOther()){
                    return@setPositiveButton
                }
                else {
                    viewModel.joinUser(
                        password,
                        nickname,
                        email,
                        dateBirth,
                        gender,
                        agreeChoice
                    )
                }
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

        binding.joinLayout.setOnClickListener {
            hideKeyboard()
        }

        binding.editTextEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                emailCheck = false
            }
        })

        binding.editTextNickname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                nicknameCheck = false
            }
        })

        observeData()
    }


    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(this@JoinActivity) {
                showToast(it)
                if (it == "회원가입에 성공했습니다."){
                    val intent = Intent(this@JoinActivity, ExplainActivity::class.java)
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


    private fun validateEmail(): Boolean {
        val value: String = binding.editTextEmail.text.toString()
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        return if (value.isEmpty()) {
            binding.editTextEmail.error = "이메일을 입력해주세요."
            false
        } else if (!value.matches(emailPattern.toRegex())) {
            binding.editTextEmail.error = "이메일 형식이 잘못되었습니다."
            false
        } else if (!emailCheck) {
            binding.editTextEmail.error = "이메일 중복확인을 해주세요."
            false
        } else {
            binding.editTextEmail.error = null
            true
        }
    }


    private fun validateNickname(): Boolean {
        val value: String = binding.editTextNickname.text.toString()

        return if (value.isEmpty()) {
            binding.editTextNickname.error = "닉네임을 입력해주세요."
            false
        }  else if (!nicknameCheck) {
            binding.editTextNickname.error = "닉네임 중복확인을 해주세요."
            false
        } else {
            binding.editTextNickname.error = null
            true
        }
    }


    private fun validatePassword(): Boolean {
        val value: String = binding.editTextPassword.text.toString()
        val valueCheck: String = binding.editTextPasswordCheck.text.toString()

        return if (value.isEmpty()) {
            binding.editTextPassword.error = "비밀번호를 입력해주세요."
            false
        } else if (valueCheck.isEmpty()) {
            binding.editTextPasswordCheck.error = "비밀번호 확인을 입력해주세요."
            false
        } else if (value != valueCheck) {
            binding.editTextPassword.error = "비밀번호와 비밀번호 확인이 다릅니다."
            binding.editTextPasswordCheck.error = "비밀번호와 비밀번호 확인이 다릅니다."
            false
        } else {
            binding.editTextPassword.error = null
            true
        }
    }


    private fun validateDateBirth(): Boolean {
        val value: String = binding.editTextDateBirth.text.toString()

        return if (value.isEmpty()) {
            binding.editTextDateBirth.error = "생년월일을 입력해주세요."
            false
        } else {
            binding.editTextDateBirth.error = null
            true
        }
    }


    private fun validateOther(): Boolean {
        return if (gender!="MALE" && gender!="FEMALE" && gender!="THIRD") {
            showToast("성별을 지정해주세요.")
            false
        } else if (!agree1) {
            showToast("이용약관을 동의 해주세요.")
            false
        } else if (!agree2) {
            showToast("개인정보 취급방침을 동의 해주세요.")
            false
        } else {
            true
        }
    }


    @SuppressLint("ShowToast")
    private fun showToast(msg: String) {
        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        } else toast?.setText(msg)
        toast?.show()
    }

    private fun hideKeyboard(){
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editTextEmail.windowToken, 0)
        imm.hideSoftInputFromWindow(binding.editTextNickname.windowToken, 0)
        imm.hideSoftInputFromWindow(binding.editTextPassword.windowToken, 0)
        imm.hideSoftInputFromWindow(binding.editTextPasswordCheck.windowToken, 0)
        imm.hideSoftInputFromWindow(binding.editTextDateBirth.windowToken, 0)
    }
}