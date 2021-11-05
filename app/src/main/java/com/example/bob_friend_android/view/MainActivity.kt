package com.example.bob_friend_android.view

import android.Manifest
import android.content.Context
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bob_friend_android.R
import com.example.bob_friend_android.adapter.SearchAdapter
import com.example.bob_friend_android.databinding.ActivityMainBinding
import com.example.bob_friend_android.model.SearchLocation
import com.example.bob_friend_android.viewmodel.MainViewModel
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    private val PERMISSIONS_REQUEST_CODE = 100
    private var REQUIRED_PERMISSIONS = arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION)

//    lateinit var fragmentMap: MapFragment
//    lateinit var fragmentList: ListFragment
    var beforeFlag:Int = 1 //프레그먼트 이전
    var flag:String = "map"

    var backKeyPressedTime: Long = 0

    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
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

//
//        binding.menu.setOnClickListener {
//            hideKeyboard()
//            binding.mainDrawerLayout.openDrawer(GravityCompat.START)
//        }


//        binding.mainWriteBtn.setOnClickListener {
//            val intent = Intent(this, CreateBoardActivity::class.java)
//            startActivity(intent)
//        }
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
}
