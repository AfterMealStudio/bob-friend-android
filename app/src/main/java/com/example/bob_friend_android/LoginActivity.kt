package com.example.bob_friend_android

import android.content.Intent
import android.graphics.Color
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.example.bob_friend_android.Fragment.MapFragment
import com.example.bob_friend_android.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        binding.registerBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        keyboardVisibilityUtils = KeyboardVisibilityUtils(window,
            onShowKeyboard = { keyboardHeight ->
                binding.svRoot.run {
                    smoothScrollTo(scrollX, scrollY + keyboardHeight)
                }
            })
    }

    override fun onDestroy() {
        keyboardVisibilityUtils.detachKeyboardListeners()
        super.onDestroy()
    }

}