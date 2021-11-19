package com.example.bob_friend_android.view.main

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bob_friend_android.R
import com.example.bob_friend_android.adapter.SearchAdapter
import com.example.bob_friend_android.databinding.FragmentMapBinding
import com.example.bob_friend_android.model.SearchLocation
import com.example.bob_friend_android.viewmodel.ListViewModel
import net.daum.mf.map.api.CalloutBalloonAdapter
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView


class MapFragment : Fragment(), MapView.MapViewEventListener {
    val TAG = "MapFragment"

    private lateinit var binding: FragmentMapBinding
    private lateinit var viewModel: ListViewModel

    private var x: Double? = null
    private var y: Double? = null
    private var placeName: String? = null
    private var click: Boolean? = false

    private lateinit var kakaoMap: ConstraintLayout
    private lateinit var mapViewContainer: ViewGroup
    lateinit var mapView: MapView

    //지도 검색 기능
    private val listItems = arrayListOf<SearchLocation>()   // 리사이클러 뷰 아이템
    private val searchAdapter = SearchAdapter(listItems)    // 리사이클러 뷰 어댑터
    private var pageNumber = 1      // 검색 페이지 번호
    private var keyword = ""        // 검색 키워드


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        Log.d(TAG, "onCreate")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)
        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        binding.lifecycleOwner = this
        binding.map = viewModel

        binding.rvList.layoutManager = LinearLayoutManager(
            requireContext(),
            LinearLayoutManager.VERTICAL,
            false
        )
        binding.rvList.adapter = searchAdapter
        binding.rvList.visibility = View.GONE

        x = arguments?.getDouble("x")
        y = arguments?.getDouble("y")
        placeName = arguments?.getString("placeName")
        click = arguments?.getBoolean("click")

        kakaoMap = binding.mapView
        mapViewContainer = kakaoMap
        mapView = MapView(requireActivity())
        mapView.setMapViewEventListener(this)
        mapViewContainer.addView(mapView)
//        mapView.visibility = View.INVISIBLE

        //커스텀 마커 추가하는 코드
        val customBalloonAdapter = CustomBalloonAdapter(layoutInflater)
        mapView.setCalloutBalloonAdapter(customBalloonAdapter)

        viewModel.setMarkers(requireContext(), this)

        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(binding.mainToolbar)
            (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        }

        binding.mainEditTextSearch.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){
                    keyword = binding.mainEditTextSearch.text.toString()
                    pageNumber = 1
                    if(keyword!="") {
                        viewModel.searchKeywordMap(keyword, searchAdapter, requireContext())
                        binding.rvList.visibility = View.VISIBLE
                    }

                    hideKeyboard()
                    return true
                }
                return false
            }
        })

        // 리스트 아이템 클릭 시 해당 위치로 이동
        searchAdapter.setItemClickListener(object : SearchAdapter.OnItemClickListener {
            override fun onClick(v: View, position: Int) {
                viewModel.setDataAtFragment(
                    this@MapFragment,
                    listItems[position].name,
                    listItems[position].y,
                    listItems[position].x
                )
                Log.d(
                    "MainActivity",
                    "argument:${arguments} x:${listItems[position].x}, y:${listItems[position].y}"
                )
                setPosition(listItems[position].y, listItems[position].x)
            }
        })

        binding.myLocation.setOnClickListener {
            setMyLocation()
        }
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mapView.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        mapView.visibility = View.GONE
    }


    fun setPosition(y: Double, x: Double) {
        val mapPoint = MapPoint.mapPointWithGeoCoord(y, x)
        mapView.setMapCenterPointAndZoomLevel(mapPoint, mapView.zoomLevel, true)
    }


    fun addMarkers(name: String, y: Double, x: Double) {
        val point = MapPOIItem()
        point.apply {
            itemName = name
            mapPoint = MapPoint.mapPointWithGeoCoord(y, x)
            customImageResourceId = R.drawable.main_color1_marker
            customSelectedImageResourceId = R.drawable.main_color2_marker
            markerType = MapPOIItem.MarkerType.CustomImage
            selectedMarkerType = MapPOIItem.MarkerType.CustomImage
            isCustomImageAutoscale = false
            setCustomImageAnchor(0.5f, 1.0f)
        }
        mapView.addPOIItem(point)
    }


    fun setMyLocation() {
        val permissionCheck = ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            val lm: LocationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            try {
                val userNowLocation: Location? =
                        lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                val uLatitude = userNowLocation!!.latitude
                val uLongitude = userNowLocation.longitude
                val uNowPosition = MapPoint.mapPointWithGeoCoord(uLatitude, uLongitude)
                mapView.setMapCenterPoint(uNowPosition, true)
                mapView.setZoomLevel(2, true)

            }catch (e: NullPointerException){
                Log.e("LOCATION_ERROR", e.toString())
                ActivityCompat.finishAffinity(requireActivity())
            }
        }else{
            Toast.makeText(requireActivity(), "위치 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onMapViewInitialized(p0: MapView?) {
        Log.d(TAG, "onMapViewInitialized")
    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {

    }

    override fun onMapViewSingleTapped(mapView: MapView?, mapPoint: MapPoint?) {
        binding.rvList.visibility = View.GONE
        hideKeyboard()
        Log.d(TAG, "onMapViewSingleTapped")
    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {
        Log.d(TAG, "onMapViewDoubleTapped")
    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {
        Log.d(TAG, "onMapViewLongPressed")
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {

    }


    private fun hideKeyboard(){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.mainEditTextSearch.windowToken, 0)
    }


    class CustomBalloonAdapter(inflater: LayoutInflater): CalloutBalloonAdapter {
        val mCalloutBalloon: View = inflater.inflate(R.layout.balloon_layout, null)
        val name: TextView = mCalloutBalloon.findViewById(R.id.ball_tv_name)

        override fun getCalloutBalloon(poiItem: MapPOIItem?): View {
            // 마커 클릭 시 나오는 말풍선
            name.text = poiItem?.itemName   // 해당 마커의 정보 이용 가능

            return mCalloutBalloon
        }

        override fun getPressedCalloutBalloon(poiItem: MapPOIItem?): View {
            // 말풍선 클릭 시
            return mCalloutBalloon
        }
    }
}

