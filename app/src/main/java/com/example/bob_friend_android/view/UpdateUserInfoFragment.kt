package com.example.bob_friend_android.view

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.base.BaseFragment
import com.example.bob_friend_android.databinding.FragmentUpdateUserInfoBinding
import com.example.bob_friend_android.viewmodel.UserViewModel

class UpdateUserInfoFragment(override val viewModel: UserViewModel) : BaseFragment<FragmentUpdateUserInfoBinding, UserViewModel>(
    R.layout.fragment_update_user_info
) {
    private var nickname: String? = null
    private var password: String? = null
    private var passwordCheck: String? = null
    private var dateBirth: String? = null
    private var gender : String? = null


    override fun init() {
        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

        binding.toolbar.title = "회원정보수정"

        observeData()

        binding.rgGender.setOnCheckedChangeListener { group, checkedId ->
            gender = when(checkedId) {
                R.id.male -> "MALE"
                R.id.female -> "FEMALE"
                R.id.third_gender -> "NONE"
                else -> ""
            }
        }

        var nicknameCheck = false
        binding.etvNickname.setOnClickListener {
            nickname = binding.etvNickname.text.toString().trim()
            viewModel.checkUserNickname(nickname!!)
            nicknameCheck = true
        }

        binding.btnUpdateUserInfo.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("회원정보수정")
            builder.setMessage("이렇게 정보 변경을 진행할까요?")

            if(binding.etvNickname.text!!.isNotBlank()){
                nickname = binding.etvNickname.text.toString().trim()
            }
            if (binding.etvPassword.text!!.isNotBlank()){
                password = binding.etvPassword.text.toString().trim()
            }
            if (binding.etvPasswordCheck.text!!.isNotBlank()){
                passwordCheck = binding.etvPasswordCheck.text.toString().trim()
            }
            if (binding.etvDateBirth.text!!.isNotBlank()){
                dateBirth = binding.etvDateBirth.text.toString().trim()
            }

            builder.setPositiveButton("예") { dialog, which ->
                if(viewModel.validationUpdate(
                        nicknameCheck = nicknameCheck, nickname = nickname,
                        password = password, gender = gender,
                        passwordCheck = passwordCheck, dateBirth = dateBirth)){
                    viewModel.updateUser(agree = null, email = null, nickname = nickname,
                        password = password, sex = gender, birth = dateBirth
                    )
                }
            }
            builder.setNegativeButton("아니오") { dialog, which ->
                return@setNegativeButton
            }
            builder.show()
        }

        binding.layoutUpdate.setOnClickListener {
            hideKeyboard()
        }
    }


    private fun hideKeyboard(){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etvNickname.windowToken, 0)
        imm.hideSoftInputFromWindow(binding.etvPassword.windowToken, 0)
        imm.hideSoftInputFromWindow(binding.etvPasswordCheck.windowToken, 0)
        imm.hideSoftInputFromWindow(binding.etvDateBirth.windowToken, 0)
    }


//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val id = item.itemId
//        when (id) {
//            android.R.id.home -> {
//                finish()
//                return true
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }


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