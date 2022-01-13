package com.example.bob_friend_android.view


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.SharedPref
import com.example.bob_friend_android.databinding.FragmentLoginBinding
import com.example.bob_friend_android.viewmodel.LoginViewModel

class LoginFragment : Fragment() {
    val TAG = "LOGIN"

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel : LoginViewModel

    private var backKeyPressedTime : Long = 0

    private var checked = false
    var toast: Toast? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.login = viewModel
        binding.lifecycleOwner = this

        SharedPref.openSharedPrep(requireContext())
        val check = App.prefs.getBoolean("checked",false)

        if (check) {
            viewModel.validateUser()
        }

        binding.loginBtn.setOnClickListener {
            val email = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            checked = binding.checkBoxAutoLogin.isChecked

            viewModel.login(email ,password)
        }

//        binding.registerBtn.setOnClickListener {
//            startActivity(Intent(requireContext(), JoinActivity::class.java))
//        }

//        binding.findUserAccountBtn.setOnClickListener {
//            startActivity(Intent(requireContext(), FindUserAccountActivity::class.java))
//        }

        binding.loginLayout.setOnClickListener {
            hideKeyboard()
        }

        observeData()

        return binding.root
    }


//    override fun onBackPressed() {
//        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
//            backKeyPressedTime = System.currentTimeMillis()
//            Toast.makeText(this, "뒤로가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
//            moveTaskToBack(true)
//            finish()
//            android.os.Process.killProcess(android.os.Process.myPid())
//        }
//    }

    private fun hideKeyboard(){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editTextEmail.windowToken, 0)
        imm.hideSoftInputFromWindow(binding.editTextPassword.windowToken, 0)
    }

    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(viewLifecycleOwner) {
//                showToast(it)
                if (it == "자동 로그인"){
//                    val intent = Intent(requireContext(), MainActivity::class.java)
//                    startActivity(intent)
                }
            }

            token.observe(viewLifecycleOwner) {
                val editor = App.prefs.edit()
                editor.putString("token", it.accessToken)
                editor.putString("refresh", it.refreshToken)
                editor.putBoolean("checked", checked)
                editor.apply()
//                val intent = Intent(requireContext(), MainActivity::class.java)
//                startActivity(intent)
            }

            refreshToken.observe(viewLifecycleOwner) {
                val editor = App.prefs.edit()
                editor.putString("token", it.accessToken)
                editor.putString("refresh", it.refreshToken)
                editor.putBoolean("checked", true)
                editor.apply()
//                val intent = Intent(this@LoginActivity, MainActivity::class.java)
//                startActivity(intent)
            }

            val dialog = LoadingDialog(requireContext())
            progressVisible.observe(viewLifecycleOwner) {
                if (progressVisible.value!!) {
                    dialog.show()
                }
                else if (!progressVisible.value!!) {
                    dialog.dismiss()
                }
            }
        }
    }


//    @SuppressLint("ShowToast")
//    private fun showToast(msg: String) {
//        if (toast == null) {
//            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
//        } else toast?.setText(msg)
//        toast?.show()
//    }
}