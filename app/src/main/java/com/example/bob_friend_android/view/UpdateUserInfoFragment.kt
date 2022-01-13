package com.example.bob_friend_android.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.FragmentUpdateUserInfoBinding
import com.example.bob_friend_android.viewmodel.UserViewModel

class UpdateUserInfoFragment: Fragment() {

    private lateinit var binding: FragmentUpdateUserInfoBinding
    private lateinit var viewModel: UserViewModel
    var toast: Toast? = null

    private var nickname: String? = null
    private var password: String? = null
    private var passwordCheck: String? = null
    private var dateBirth: String? = null
    private var gender : String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_update_user_info, container, false)
        viewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        binding.lifecycleOwner = this
        binding.list = viewModel

        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.toolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
            (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }

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
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("회원정보수정")
                builder.setMessage("이렇게 정보 변경을 진행할까요?")

                if(binding.editTextTextNickname.text!!.isNotBlank()){
                        nickname = binding.editTextTextNickname.text.toString().trim()
                }
                if (binding.editTextTextPassword.text!!.isNotBlank()){
                    password = binding.editTextTextPassword.text.toString().trim()
                }
                if (binding.editTextTextPasswordCheck.text!!.isNotBlank()){
                    passwordCheck = binding.editTextTextPasswordCheck.text.toString().trim()
                }
                if (binding.editTextTextBirth.text!!.isNotBlank()){
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
                        }
                }
                builder.setNegativeButton("아니오") { dialog, which ->
                    return@setNegativeButton
                }
                builder.show()
        }

        binding.updateLayout.setOnClickListener {
            hideKeyboard()
        }

        return binding.root
    }


    private fun hideKeyboard(){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editTextTextBirth.windowToken, 0)
        imm.hideSoftInputFromWindow(binding.editTextTextNickname.windowToken, 0)
        imm.hideSoftInputFromWindow(binding.editTextTextPassword.windowToken, 0)
        imm.hideSoftInputFromWindow(binding.editTextTextPasswordCheck.windowToken, 0)
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

    @SuppressLint("ShowToast")
    private fun showToast(msg: String) {
        if (toast == null) {
//            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        } else toast?.setText(msg)
        toast?.show()
    }

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