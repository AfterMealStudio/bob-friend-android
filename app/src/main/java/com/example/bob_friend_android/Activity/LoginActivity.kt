package com.example.bob_friend_android.Activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog

import androidx.appcompat.app.AppCompatActivity
import com.example.bob_friend_android.DataModel.Login
import com.example.bob_friend_android.KeyboardVisibilityUtils
import com.example.bob_friend_android.LoginService
import com.example.bob_friend_android.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var keyboardVisibilityUtils: KeyboardVisibilityUtils
    private var BASE_URL = "http://172.30.1.50:8000" //서버 ip 넣어주기

    var login:Login? = null
    var retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    var loginService: LoginService = retrofit.create(LoginService::class.java)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginBtn.setOnClickListener {
            val text1 = binding.editTextEmail.text.toString()
            val text2 = binding.editTextPassword.text.toString()

            val intent = Intent(this, MainActivity::class.java)

            loginService.requestLogin(text1,text2).enqueue(object: Callback<Login> {
                override fun onFailure(call: Call<Login>, t: Throwable) {
                    Log.e("LOGIN",t.message!!)
                    val dialog = AlertDialog.Builder(this@LoginActivity)
                    dialog.setTitle("에러")
                    dialog.setMessage("호출실패했습니다.")
                    dialog.show()
                }

                override fun onResponse(call: Call<Login>, response: Response<Login>) {
                    login = response.body()
                    Log.d("LOGIN","msg : "+login?.msg)
                    Log.d("LOGIN","code : "+login?.code)
                    val dialog = AlertDialog.Builder(this@LoginActivity)
                    dialog.setTitle(login?.msg)
                    dialog.setMessage(login?.code)
                    dialog.show()
                    //startActivity(intent)
                }
            })
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