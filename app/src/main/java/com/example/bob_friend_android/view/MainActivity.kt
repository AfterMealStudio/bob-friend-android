package com.example.bob_friend_android.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bob_friend_android.adapter.SearchAdapter
import com.example.bob_friend_android.model.SearchLocation
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.ActivityMainBinding
import com.example.bob_friend_android.onBackPressedListener
import com.example.bob_friend_android.viewmodel.MainViewModel
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    private val PERMISSIONS_REQUEST_CODE = 100
    private var REQUIRED_PERMISSIONS = arrayOf<String>( Manifest.permission.ACCESS_FINE_LOCATION)

    lateinit var fragmentMap: MapFragment
    lateinit var fragmentList: ListFragment
//    lateinit var fragmentSetting: SettingFragment
//    lateinit var fragmentLogout: LogoutFragment
//    lateinit var fragmentAppointment: MyAppointmentFragment
//    lateinit var fragmentBoard: MyBoardFragment
//    lateinit var fragmentAbout: AboutFragment

    var beforeFlag:Int = 1 //프레그먼트 이전
    var flag:Int = 1

    var backKeyPressedTime: Long = 0

    //지도 검색 기능
    private val listItems = arrayListOf<SearchLocation>()   // 리사이클러 뷰 아이템
    private val searchAdapter = SearchAdapter(listItems)    // 리사이클러 뷰 어댑터
    private var pageNumber = 1      // 검색 페이지 번호
    private var keyword = ""        // 검색 키워드

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

        binding.rvList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = searchAdapter
        binding.rvList.visibility = View.GONE

        fragmentMap = MapFragment()
        fragmentList = ListFragment()
//        fragmentSetting = SettingFragment()
//        fragmentAbout = AboutFragment()
//        fragmentAppointment = MyAppointmentFragment()
//        fragmentBoard = MyBoardFragment()
//        fragmentLogout = LogoutFragment()

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)


        val header =  binding.mainNavigationView.getHeaderView(0)
        val headerUserName = header.findViewById<TextView>(R.id.header_username)
        val headerEmail = header.findViewById<TextView>(R.id.header_email)
        viewModel.setUserInfo(headerUserName, headerEmail)

        //위치정보 퍼미션
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE )

        setIconColor()
        setFragment()

        setSupportActionBar(binding.mainToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.mapToggleBtn.setOnClickListener {
            hideKeyboard()
            setFragment()
        }

        binding.mainNavigationView.setNavigationItemSelectedListener(this)

        binding.menu.setOnClickListener {
            hideKeyboard()
            binding.mainDrawerLayout.openDrawer(GravityCompat.START)
        }


        binding.mainWriteBtn.setOnClickListener {
            val intent = Intent(this, CreateBoardActivity::class.java)
            startActivity(intent)
        }

        binding.myLocation.setOnClickListener {
            fragmentMap.setMyLocation()
        }

        binding.mainEditTextSearch.visibility = View.INVISIBLE
        binding.search.setOnClickListener {
            binding.mainEditTextSearch.visibility = View.VISIBLE

            keyword = binding.mainEditTextSearch.text.toString()
            pageNumber = 1
            if(keyword!="") {
                viewModel.searchKeyword(keyword, searchAdapter,this)
                binding.rvList.visibility = View.VISIBLE
                hideKeyboard()
            }
        }


        // 리스트 아이템 클릭 시 해당 위치로 이동
        searchAdapter.setItemClickListener(object: SearchAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                viewModel.setDataAtFragment(fragmentMap, listItems[position].name, listItems[position].y, listItems[position].x)
                Log.d("MainActivity", "argument:${fragmentMap.arguments} x:${listItems[position].x}, y:${listItems[position].y}")
                fragmentMap.setPosition(listItems[position].y, listItems[position].x)
            }
        })

        binding.frameLayout.setOnClickListener {
            hideKeyboard()
        }
    }


    private fun setFragment() {
        val transaction = supportFragmentManager.beginTransaction()
        when(flag){
            1 -> {
                binding.myLocation.visibility = View.VISIBLE
                transaction.replace(R.id.frameLayout, fragmentMap)
                flag = 2
            }
            2 -> {
                binding.myLocation.visibility = View.INVISIBLE
                transaction.replace(R.id.frameLayout, fragmentList)
                flag = 1
            }
//            3 -> {
//                transaction.replace(R.id.frameLayout, fragmentBoard)
//            }
//            4 -> {
//                transaction.replace(R.id.frameLayout, fragmentAppointment)
//            }
//            5 -> {
//                transaction.replace(R.id.frameLayout, fragmentSetting)
//            }
//            6 -> {
//                transaction.replace(R.id.frameLayout, fragmentLogout)
//            }
//            7 -> {
//                transaction.replace(R.id.frameLayout, fragmentAbout)
//            }
        }
        transaction.commit()
    }


    private fun setIconColor() {
        binding.mainWriteBtn.setColorFilter(Color.parseColor("#0A1931"))
        binding.menu.setColorFilter(Color.parseColor("#FFFFFFFF"))
        binding.search.setColorFilter(Color.parseColor("#FFFFFFFF"))
    }


    override fun onBackPressed() {
        if(System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis()
            hideKeyboard()
            Toast.makeText(this, "뒤로가기 버튼을 한 번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
            return
        }
        if (System.currentTimeMillis() < backKeyPressedTime + 2500) {
            finishAffinity()
        }
    }


    private fun hideKeyboard(){
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.mainEditTextSearch.windowToken, 0)
    }


    override fun onResume() {
        fragmentMap.mapView.visibility = View.VISIBLE
        super.onResume()
    }


    override fun onPause() {
        fragmentMap.mapView.visibility = View.INVISIBLE
        super.onPause()
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.myBoard -> setFragmentUserInfo(flag, 3)
            R.id.myAppointmentList -> setFragmentUserInfo(flag, 4)
            R.id.userSetting -> setFragmentUserInfo(flag, 5)
            R.id.logout -> setFragmentUserInfo(flag, 6)
            R.id.about -> setFragmentUserInfo(flag, 7)
        }
        return true
    }

    fun setFragmentUserInfo(beforeFlag: Int, afterFlag:Int) {
        flag = afterFlag
        setFragment()
        flag = beforeFlag
        binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
    }
}
