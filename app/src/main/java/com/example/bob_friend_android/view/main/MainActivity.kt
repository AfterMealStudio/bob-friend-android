package com.example.bob_friend_android.view.main

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.ActivityMainBinding
import com.example.bob_friend_android.view.DetailBoardActivity
import com.example.bob_friend_android.viewmodel.MainViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    private val PERMISSIONS_REQUEST_CODE = 100
    private var REQUIRED_PERMISSIONS = arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION)

    var flag:String = "map"
    var toast: Toast? = null

    var backKeyPressedTime: Long = 0

    companion object {
        const val API_KEY = "KakaoAK 81e4657cca25cf97b1cec85102769390"  // REST API 키
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        binding.lifecycleOwner = this
        binding.main = viewModel

        //위치정보 퍼미션
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE)

        val fragmentA = MapFragment()
        supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment, fragmentA).commit()
        initNavigationBar()

        viewModel.setUserInfo()

        observeData()
    }

    private fun initNavigationBar() {
        binding.bottomNavigationView.run {
            setOnItemSelectedListener { item ->
                when(item.itemId) {
                    R.id.setMap -> {
                        val fragmentA = MapFragment()
                        supportFragmentManager.beginTransaction().replace(
                            R.id.nav_host_fragment,
                            fragmentA
                        ).commitAllowingStateLoss()
                    }
                    R.id.setList -> {
                        val fragmentB = ListFragment()
                        supportFragmentManager.beginTransaction().replace(
                            R.id.nav_host_fragment,
                            fragmentB
                        ).commitAllowingStateLoss()
                    }
                    R.id.addBoard -> {
                        val fragmentC = CreateBoardFragment()
                        supportFragmentManager.beginTransaction().replace(
                            R.id.nav_host_fragment,
                            fragmentC,
                            "appointment"
                        ).commitAllowingStateLoss()
                    }
                    R.id.userSetting -> {
                        val fragmentD = SettingFragment()
                        supportFragmentManager.beginTransaction().replace(
                            R.id.nav_host_fragment,
                            fragmentD,
                            "about"
                        ).commitAllowingStateLoss()
                    }
                }
                true
            }
        }
    }


    override fun onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 4000) {
            backKeyPressedTime = System.currentTimeMillis()
            Toast.makeText(this, "뒤로가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            return
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 4000) {
            super.onBackPressed()
        }
    }


    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(this@MainActivity) {
                showToast(it)
            }

            userInfo.observe(this@MainActivity, { user ->
                val editor = App.prefs.edit()
                editor.putInt("id", user.id)
                editor.putString("email", user.email)
                editor.putString("nickname", user.nickname)
                editor.putString("birth", user.birth)
                editor.putString("sex", user.sex)
                editor.putBoolean("agree", user.agree)
                editor.putFloat("rating", user.rating)
                editor.apply()
            })
        }
    }


    private fun showToast(msg: String) {
        if (toast == null) {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT)
        } else toast?.setText(msg)
        toast?.show()
    }
}
