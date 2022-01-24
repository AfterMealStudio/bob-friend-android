package com.example.bob_friend_android.ui.view

import android.content.Context
import android.content.Intent
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.activityViewModels
import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.SharedPref
import com.example.bob_friend_android.ui.view.base.BaseFragment
import com.example.bob_friend_android.databinding.FragmentLoginBinding
import com.example.bob_friend_android.ui.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(
    R.layout.fragment_login
) {
    private val viewModel by activityViewModels<LoginViewModel>()
    private var checked = false


    override fun init() {
        SharedPref.openSharedPrep(requireContext())

        val check = App.prefs.getBoolean("checked",false)
        if (check) {
            viewModel.validateUser()
        }

        binding.btnLogin.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            checked = binding.checkBoxAutoLogin.isChecked

            viewModel.login(email ,password)
        }

        binding.btnRegister.setOnClickListener {
            goToNext(R.id.action_loginFragment_to_joinFragment)
        }

        binding.btnFindUserAccount.setOnClickListener {
            goToNext(R.id.action_loginFragment_to_findUserAccountFragment)
        }

        binding.loginLayout.setOnClickListener {
            hideKeyboard()
        }

        observeData()
    }

    private fun hideKeyboard(){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editTextEmail.windowToken, 0)
        imm.hideSoftInputFromWindow(binding.editTextPassword.windowToken, 0)
    }

    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(viewLifecycleOwner) {
                showToast(it)
                if (it == "자동 로그인"){
                    val intent = Intent(context, SetHomeActivity::class.java)
                    startActivity(intent)
                    activity?.finish()
                }
            }

            token.observe(viewLifecycleOwner) {
                val editor = App.prefs.edit()
                editor.putString("token", it.accessToken)
                editor.putString("refresh", it.refreshToken)
                val check = App.prefs.getBoolean("checked", false)
                if (!check) {
                    editor.putBoolean("checked", checked)
                }
                editor.apply()

                val intent = Intent(context, SetHomeActivity::class.java)
                startActivity(intent)
                activity?.finish()
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
}