package com.example.bob_friend_android

import android.graphics.Color
import android.os.Bundle

import androidx.appcompat.app.AppCompatActivity
import com.example.bob_friend_android.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        iconColorChange()
    }

    private fun iconColorChange() {
        binding.mainIcon.setColorFilter(Color.parseColor("#FFFFFFFF"))

    }
}