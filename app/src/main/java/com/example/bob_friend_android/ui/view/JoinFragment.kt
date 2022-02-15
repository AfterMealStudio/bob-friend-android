package com.example.bob_friend_android.ui.view

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CompoundButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.FragmentBoardBinding
import com.example.bob_friend_android.ui.view.base.BaseFragment
import com.example.bob_friend_android.databinding.FragmentJoinBinding
import com.example.bob_friend_android.ui.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.properties.Delegates

@AndroidEntryPoint
class JoinFragment : BaseFragment<FragmentJoinBinding>(
    R.layout.fragment_join
) {
    private val viewModel by activityViewModels<UserViewModel>()

    val TAG = "JoinActivity"

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

    override fun onCreateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentJoinBinding {
        return FragmentJoinBinding.inflate(inflater, container, false).apply {
            lifecycleOwner = viewLifecycleOwner
        }
    }

    override fun init() {
        gender = ""
        requireDataBinding().registerGenderGroup.setOnCheckedChangeListener { group, checkedId ->
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
                        requireDataBinding().agree1.isChecked = true
                        requireDataBinding().agree2.isChecked = true
                        requireDataBinding().agree3.isChecked = true
                    }
                    R.id.agree1 -> if (requireDataBinding().agree1.isChecked && requireDataBinding().agree2.isChecked && requireDataBinding().agree3.isChecked) requireDataBinding().agreeAll.isChecked =
                        true
                    R.id.agree2 -> if (requireDataBinding().agree1.isChecked && requireDataBinding().agree2.isChecked && requireDataBinding().agree3.isChecked) requireDataBinding().agreeAll.isChecked =
                        true
                    R.id.agree3 -> if (requireDataBinding().agree1.isChecked && requireDataBinding().agree2.isChecked && requireDataBinding().agree3.isChecked) requireDataBinding().agreeAll.isChecked =
                        true
                }
            }

            else {
                when(buttonView.id) {
                    R.id.agree1 -> requireDataBinding().agreeAll.isChecked = false
                    R.id.agree2 -> requireDataBinding().agreeAll.isChecked = false
                    R.id.agree3 -> requireDataBinding().agreeAll.isChecked = false
                }
            }
        }

        requireDataBinding().agreeAll.setOnCheckedChangeListener(checkListener)
        requireDataBinding().agree1.setOnCheckedChangeListener(checkListener)
        requireDataBinding().agree2.setOnCheckedChangeListener(checkListener)
        requireDataBinding().agree3.setOnCheckedChangeListener(checkListener)


        requireDataBinding().joinBtn.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("회원가입")
            builder.setMessage("이렇게 회원가입을 진행할까요?")

            nickname = requireDataBinding().editTextNickname.text.toString().trim()
            password = requireDataBinding().editTextPassword.text.toString().trim()
            passwordCheck = requireDataBinding().editTextPasswordCheck.text.toString().trim()
            email = requireDataBinding().editTextEmail.text.toString().trim()
            dateBirth = requireDataBinding().editTextDateBirth.text.toString().trim()
            agreeAll = requireDataBinding().agreeAll.isChecked
            agree1 = requireDataBinding().agree1.isChecked
            agree2 = requireDataBinding().agree2.isChecked
            agreeChoice = requireDataBinding().agree3.isChecked

            builder.setPositiveButton("예") { dialog, which ->
                if (!validateEmail() || !validateNickname() || !validatePassword() || !validateDateBirth() || !validateOther()){
                    return@setPositiveButton
                }
                else {
                    viewModel.signUp(
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

        requireDataBinding().emailCheck.setOnClickListener {
            email = requireDataBinding().editTextEmail.text.toString().trim()
            Log.d(TAG, "email: $email")
            viewModel.checkUserEmail(email)
            emailCheck = true
        }

        requireDataBinding().nicknameCheck.setOnClickListener {
            nickname = requireDataBinding().editTextNickname.text.toString().trim()
            Log.d(TAG, "nickname: $nickname")
            viewModel.checkUserNickname(nickname)
            nicknameCheck = true
        }

        requireDataBinding().joinLayout.setOnClickListener {
            hideKeyboard()
        }

        requireDataBinding().editTextEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                emailCheck = false
            }
        })

        requireDataBinding().editTextNickname.addTextChangedListener(object : TextWatcher {
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
            errorMsg.observe(viewLifecycleOwner) {
                showToast(it)
                if (it == "회원가입에 성공했습니다."){
                    goToNext(R.id.action_joinFragment_to_explainJoinFragment)
                }
            }

            val dialog = SetLoadingDialog(requireContext())
            isLoading.observe(viewLifecycleOwner) {
                if (isLoading.value!!) {
                    dialog.show()
                }
                else if (!isLoading.value!!) {
                    dialog.dismiss()
                }
            }
        }
    }


    private fun validateEmail(): Boolean {
        val value: String = requireDataBinding().editTextEmail.text.toString()
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

        return if (value.isEmpty()) {
            requireDataBinding().editTextEmail.error = "이메일을 입력해주세요."
            false
        } else if (!value.matches(emailPattern.toRegex())) {
            requireDataBinding().editTextEmail.error = "이메일 형식이 잘못되었습니다."
            false
        } else if (!emailCheck) {
            requireDataBinding().editTextEmail.error = "이메일 중복확인을 해주세요."
            false
        } else {
            requireDataBinding().editTextEmail.error = null
            true
        }
    }


    private fun validateNickname(): Boolean {
        val value: String = requireDataBinding().editTextNickname.text.toString()

        return if (value.isEmpty()) {
            requireDataBinding().editTextNickname.error = "닉네임을 입력해주세요."
            false
        }  else if (!nicknameCheck) {
            requireDataBinding().editTextNickname.error = "닉네임 중복확인을 해주세요."
            false
        } else {
            requireDataBinding().editTextNickname.error = null
            true
        }
    }


    private fun validatePassword(): Boolean {
        val value: String = requireDataBinding().editTextPassword.text.toString()
        val valueCheck: String = requireDataBinding().editTextPasswordCheck.text.toString()
        val passwordPattern = "^(?=.*[a-zA-Z0-9])(?=.*[a-zA-Z!@#\$%^&*])(?=.*[0-9!@#\$%^&*]).{8,}\$"

        return if (value.isEmpty()) {
            requireDataBinding().editTextPassword.error = "비밀번호를 입력해주세요."
            false
        } else if (valueCheck.isEmpty()) {
            requireDataBinding().editTextPasswordCheck.error = "비밀번호 확인을 입력해주세요."
            false
        } else if (!value.matches(passwordPattern.toRegex())) {
            requireDataBinding().editTextPasswordCheck.error = "숫자, 문자, 특수문자를 포함하여 8자 이상으로 구성해주세요."
            false
        } else if (value != valueCheck) {
            requireDataBinding().editTextPassword.error = "비밀번호와 비밀번호 확인이 다릅니다."
            requireDataBinding().editTextPasswordCheck.error = "비밀번호와 비밀번호 확인이 다릅니다."
            false
        } else {
            requireDataBinding().editTextPassword.error = null
            true
        }
    }


    private fun validateDateBirth(): Boolean {
        val value: String = requireDataBinding().editTextDateBirth.text.toString()

        val currentDate = Calendar.getInstance().time
        val format = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val currentDateFormat = format.format(currentDate)

        var age = currentDateFormat.substring(0,4).toInt() - value.substring(0,4).toInt()
        if (currentDateFormat.substring(5,8).toInt() - value.substring(5,8).toInt() > 0) {
            age += 1
        }

        return if (value.isEmpty()) {
            requireDataBinding().editTextDateBirth.error = "생년월일을 입력해주세요."
            false
        } else if(age < 15) {
            requireDataBinding().editTextDateBirth.error = "만 14세 미만의 이용자는 해당 서비스를 이용할 수 없습니다."
            false
        } else {
            requireDataBinding().editTextDateBirth.error = null
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


    private fun hideKeyboard(){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(requireDataBinding().editTextEmail.windowToken, 0)
        imm.hideSoftInputFromWindow(requireDataBinding().editTextNickname.windowToken, 0)
        imm.hideSoftInputFromWindow(requireDataBinding().editTextPassword.windowToken, 0)
        imm.hideSoftInputFromWindow(requireDataBinding().editTextPasswordCheck.windowToken, 0)
        imm.hideSoftInputFromWindow(requireDataBinding().editTextDateBirth.windowToken, 0)
    }
}