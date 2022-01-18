package com.example.bob_friend_android.view

import android.os.Bundle
import com.example.bob_friend_android.R
import com.example.bob_friend_android.base.BaseActivity
import com.example.bob_friend_android.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    var backKeyPressedTime: Long = 0

    override fun onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 4000) {
            backKeyPressedTime = System.currentTimeMillis()
            showToast("뒤로가기 버튼을 한 번 더 누르면 종료됩니다.")
            return
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 4000) {
            super.onBackPressed()
        }
    }
}
