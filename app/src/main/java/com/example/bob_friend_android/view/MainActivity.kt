package com.example.bob_friend_android.view

import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.example.bob_friend_android.R
import com.example.bob_friend_android.base.BaseActivity
import com.example.bob_friend_android.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
//    lateinit var navController: NavController
//    var backKeyPressedTime: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


//    override fun onBackPressed() {
//        if (System.currentTimeMillis() > backKeyPressedTime + 4000) {
//            backKeyPressedTime = System.currentTimeMillis()
//            Toast.makeText(this, "뒤로가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        if (System.currentTimeMillis() <= backKeyPressedTime + 4000) {
//            super.onBackPressed()
//        }
//    }
}
