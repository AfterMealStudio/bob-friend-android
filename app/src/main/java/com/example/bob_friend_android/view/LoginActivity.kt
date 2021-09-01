package com.example.bob_friend_android.view


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.App
import com.example.bob_friend_android.KeyboardVisibilityUtils
import com.example.bob_friend_android.R
import com.example.bob_friend_android.SharedPref
import com.example.bob_friend_android.databinding.ActivityLoginBinding
import com.example.bob_friend_android.viewmodel.LoginViewModel

class LoginActivity : AppCompatActivity() {

    val TAG = "LOGIN"
    val PREFERENCE = "bob_friend_android"

    private lateinit var binding: ActivityLoginBinding
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils
    private lateinit var viewModel : LoginViewModel

    private var backKeyPressedTime : Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        binding.login = this
        binding.lifecycleOwner = this

        SharedPref.openSharedPrep(this)
        val pref = getSharedPreferences(PREFERENCE, MODE_PRIVATE)
        val token = pref.getString("token","")
        val check = pref.getBoolean("checked",false)

        Log.d(TAG, "tt: $token, $check")
        if (token != "" && check) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.loginBtn.setOnClickListener {
            val username = binding.editTextEmail.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            val checked = binding.checkBoxAutoLogin.isChecked
            Log.d(TAG, "username: $username")

            viewModel.login(username ,password, checked,this)
        }

        binding.registerBtn.setOnClickListener {
            startActivity(Intent(this@LoginActivity, JoinActivity::class.java))
        }

        keyboardVisibilityUtils = KeyboardVisibilityUtils(window,
            onShowKeyboard = { keyboardHeight ->
                binding.svRoot.run {
                    smoothScrollTo(scrollX, scrollY + keyboardHeight)
                }
        })
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
}