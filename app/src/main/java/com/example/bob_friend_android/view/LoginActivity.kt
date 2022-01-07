package com.example.bob_friend_android.view


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.SharedPref
import com.example.bob_friend_android.databinding.ActivityLoginBinding
import com.example.bob_friend_android.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {
    val TAG = "LOGIN"

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel : LoginViewModel

    private var backKeyPressedTime : Long = 0

    private var checked = false
    var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.login = this
        binding.lifecycleOwner = this

        SharedPref.openSharedPrep(this)
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

        binding.registerBtn.setOnClickListener {
            startActivity(Intent(this@LoginActivity, JoinActivity::class.java))
        }

        binding.findUserAccountBtn.setOnClickListener {
            startActivity(Intent(this@LoginActivity, FindUserAccountActivity::class.java))
        }

        binding.loginLayout.setOnClickListener {
            hideKeyboard()
        }

        observeData()
    }


    override fun onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "뒤로가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            moveTaskToBack(true)
            finish()
            android.os.Process.killProcess(android.os.Process.myPid())
        }
    }

    private fun hideKeyboard(){
        val imm = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.editTextEmail.windowToken, 0)
        imm.hideSoftInputFromWindow(binding.editTextPassword.windowToken, 0)
    }

    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(this@LoginActivity) {
                showToast(it)
                if (it == "자동 로그인"){
                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(intent)
                }
            }

            token.observe(this@LoginActivity) {
                val editor = App.prefs.edit()
                editor.putString("token", it.accessToken)
                editor.putString("refresh", it.refreshToken)
                editor.putBoolean("checked", checked)
                editor.apply()
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
            }

            refreshToken.observe(this@LoginActivity) {
                val editor = App.prefs.edit()
                editor.putString("token", it.accessToken)
                editor.putString("refresh", it.refreshToken)
                editor.putBoolean("checked", true)
                editor.apply()
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
            }

            val dialog = LoadingDialog(this@LoginActivity)
            progressVisible.observe(this@LoginActivity) {
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