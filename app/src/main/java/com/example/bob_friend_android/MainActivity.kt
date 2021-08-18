package com.example.bob_friend_android

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import com.example.bob_friend_android.Fragment.ListFragment
import com.example.bob_friend_android.Fragment.MapFragment
import com.example.bob_friend_android.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val PERMISSIONS_REQUEST_CODE = 100
    private var REQUIRED_PERMISSIONS = arrayOf<String>( Manifest.permission.ACCESS_FINE_LOCATION)
    var flag = 1
    var backKeyPressedTime: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //위치정보 퍼미션
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE )


        setSupportActionBar(binding.mainToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.mapToggleBtn.setOnClickListener { setFragment() }

        binding.menu.setOnClickListener { binding.mainDrawerLayout.openDrawer(GravityCompat.START) }

        binding.mainEditTextSearch.visibility = View.INVISIBLE
        binding.search.setOnClickListener { binding.mainEditTextSearch.visibility = View.VISIBLE }

        binding.mainWriteBtn.setOnClickListener {
            val intent = Intent(this, WriteBoardActivity::class.java)
            startActivity(intent)
        }
        iconColorChange()
        setFragment()
    }


    private fun iconColorChange() {
        binding.mainWriteBtn.setColorFilter(Color.parseColor("#0A1931"))
        binding.menu.setColorFilter(Color.parseColor("#FFFFFFFF"))
        binding.search.setColorFilter(Color.parseColor("#FFFFFFFF"))
    }


    private fun setFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        when(flag){
            1 -> {
                transaction.replace(R.id.frameLayout, MapFragment())
                flag = 2
            }
            2 -> {
                transaction.replace(R.id.frameLayout, ListFragment())
                flag = 1
            }
        }
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onBackPressed() {
        if(System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis()
            return
        }
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            finishAffinity()
        }
    }
}
