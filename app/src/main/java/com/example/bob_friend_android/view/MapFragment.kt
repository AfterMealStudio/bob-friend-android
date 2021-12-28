package com.example.bob_friend_android.view

import android.annotation.SuppressLint
import android.content.Context
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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bob_friend_android.R
import com.example.bob_friend_android.adapter.SearchAdapter
import com.example.bob_friend_android.databinding.FragmentMapBinding
import com.example.bob_friend_android.model.Location
import com.example.bob_friend_android.model.SearchLocation
import com.example.bob_friend_android.viewmodel.ListViewModel
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource


class MapFragment : Fragment(), OnMapReadyCallback {
    val TAG = "MapFragment"

    private lateinit var binding: FragmentMapBinding
    private lateinit var viewModel: ListViewModel

    private var x: Double? = null
    private var y: Double? = null
    private var placeName: String? = null
    private var click: Boolean? = false

    lateinit var mapView: MapView
    private val LOCATION_PERMISSTION_REQUEST_CODE: Int = 1000
    private lateinit var locationSource: FusedLocationSource // 위치를 반환하는 구현체
    private lateinit var naverMap: NaverMap

    //지도 검색 기능
    private val listItems = arrayListOf<SearchLocation>()   // 리사이클러 뷰 아이템
    private val searchAdapter = SearchAdapter(listItems)    // 리사이클러 뷰 어댑터
    private var pageNumber = 1      // 검색 페이지 번호
    private var keyword = ""        // 검색 키워드

    var toast: Toast? = null

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

        mapView = binding.mapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        locationSource = FusedLocationSource(this, LOCATION_PERMISSTION_REQUEST_CODE)

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
                        viewModel.searchKeywordMap(keyword)
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
                setPosition(listItems[position].y, listItems[position].x)
            }
        })

        observeData()

        return binding.root
    }


    private fun hideKeyboard(){
        val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.mainEditTextSearch.windowToken, 0)
    }


    private fun observeData() {
        with(viewModel) {
            errorMsg.observe(viewLifecycleOwner) {
                showToast(it)
            }

            searchKeyword.observe(viewLifecycleOwner) {
                if (!it?.documents.isNullOrEmpty()) {
                    listItems.clear()
                    for(document in it.documents) {
                        val item = SearchLocation(document.place_name,
                            document.road_address_name,
                            document.address_name,
                            document.x.toDouble(),
                            document.y.toDouble())

                        listItems.add(item)
                    }
                    searchAdapter.addItems(listItems)
                }
            }

            location.observe(viewLifecycleOwner) {
                for (document in it) {
                    addMarkers(document)
                }
            }
        }
    }


    private fun addMarkers(item: Location) {
        val marker = Marker()
        marker.position = LatLng(item.latitude, item.longitude)
        marker.map = naverMap
    }


    fun setPosition(y: Double, x: Double) {
        val cameraPosition = CameraPosition(
            LatLng(y, x),
            14.0
        )
        naverMap.cameraPosition = cameraPosition
    }


    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
        naverMap.uiSettings.isLocationButtonEnabled = true
        naverMap.setOnMapClickListener { point, coord ->
            binding.rvList.visibility = View.GONE
        }

        naverMap.addOnCameraChangeListener { reason, animated ->
            Log.i("NaverMap", "카메라 변경 - reson: $reason, animated: $animated")
            val cameraPositionLatitude = naverMap.cameraPosition.target.latitude
            val cameraPositionLongitude = naverMap.cameraPosition.target.longitude
            viewModel.setMarkers(10, cameraPositionLongitude, cameraPositionLatitude)
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    @SuppressLint("ShowToast")
    private fun showToast(msg: String) {
        if (toast == null) {
            toast = Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT)
        } else toast?.setText(msg)
        toast?.show()
    }
}

