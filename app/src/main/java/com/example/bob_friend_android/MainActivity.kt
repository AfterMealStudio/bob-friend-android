package com.example.bob_friend_android

import android.Manifest
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bob_friend_android.Adapter.SearchAdapter
import com.example.bob_friend_android.DataModel.SearchKeyword
import com.example.bob_friend_android.DataModel.SearchLocation
import com.example.bob_friend_android.Fragment.ListFragment
import com.example.bob_friend_android.Fragment.MapFragment
import com.example.bob_friend_android.databinding.ActivityMainBinding
import com.example.bob_friend_android.databinding.FragmentMapBinding
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class MainActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val bindingMap by lazy { FragmentMapBinding.inflate(layoutInflater) }
    private val PERMISSIONS_REQUEST_CODE = 100
    private var REQUIRED_PERMISSIONS = arrayOf<String>( Manifest.permission.ACCESS_FINE_LOCATION)

    var flag = 1 //프레그먼트 교체

    var backKeyPressedTime: Long = 0

    //지도 검색 기능
    private val listItems = arrayListOf<SearchLocation>()   // 리사이클러 뷰 아이템
    private val listAdapter = SearchAdapter(listItems)    // 리사이클러 뷰 어댑터
    private var pageNumber = 1      // 검색 페이지 번호
    private var keyword = ""        // 검색 키워드

    companion object {
        const val BASE_URL = "https://dapi.kakao.com/"
        const val API_KEY = "KakaoAK 81e4657cca25cf97b1cec85102769390"  // REST API 키
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //위치정보 퍼미션
        ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, PERMISSIONS_REQUEST_CODE )

        binding.rvList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvList.adapter = listAdapter
        // 리스트 아이템 클릭 시 해당 위치로 이동
        listAdapter.setItemClickListener(object: SearchAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                val mapPoint = MapPoint.mapPointWithGeoCoord(listItems[position].y, listItems[position].x)
                bindingMap.mapView.setMapCenterPointAndZoomLevel(mapPoint, 1, true)
            }
        })

        // 검색 버튼
        binding.search.setOnClickListener {
            keyword = binding.mainEditTextSearch.text.toString()
            pageNumber = 1
            searchKeyword(keyword)
        }


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

        searchKeyword("은행")
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


    private fun searchKeyword(keyword: String) {
        val retrofit = Retrofit.Builder()   // Retrofit 구성
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)   // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(API_KEY, keyword)   // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object: Callback<SearchKeyword> {
            override fun onResponse(call: Call<SearchKeyword>, response: Response<SearchKeyword>) {
                // 통신 성공
                addItemsAndMarkers(response.body())
            }

            override fun onFailure(call: Call<SearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w("LocalSearch", "통신 실패: ${t.message}")
            }
        })
    }


    private fun addItemsAndMarkers(searchResult: SearchKeyword?) {
        if (!searchResult?.documents.isNullOrEmpty()) {
            // 검색 결과 있음
            listItems.clear()                   // 리스트 초기화
            bindingMap.mapView.removeAllPOIItems() // 지도의 마커 모두 제거
            for (document in searchResult!!.documents) {
                // 결과를 리사이클러 뷰에 추가
                val item = SearchLocation(document.place_name,
                    document.road_address_name,
                    document.address_name,
                    document.x.toDouble(),
                    document.y.toDouble())
                listItems.add(item)

                // 지도에 마커 추가
                val point = MapPOIItem()
                point.apply {
                    itemName = document.place_name
                    mapPoint = MapPoint.mapPointWithGeoCoord(document.y.toDouble(),
                        document.x.toDouble())
                    markerType = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }
                bindingMap.mapView.addPOIItem(point)
            }
            listAdapter.notifyDataSetChanged()

//            binding.btnNextPage.isEnabled = !searchResult.meta.is_end // 페이지가 더 있을 경우 다음 버튼 활성화
//            binding.btnPrevPage.isEnabled = pageNumber != 1             // 1페이지가 아닐 경우 이전 버튼 활성화

        } else {
            // 검색 결과 없음
            Toast.makeText(this, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }
}
