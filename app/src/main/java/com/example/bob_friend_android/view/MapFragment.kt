package com.example.bob_friend_android.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.FragmentListBinding
import com.example.bob_friend_android.databinding.FragmentMapBinding
import com.example.bob_friend_android.viewmodel.ListViewModel
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView


class MapFragment : Fragment() {
    private lateinit var binding: FragmentMapBinding
    private lateinit var viewModel: ListViewModel

    private var x: Double? = null
    private var y: Double? = null
    private var placeName: String? = null
    private var click: Boolean? = false

    private lateinit var kakaoMap: ConstraintLayout
    private lateinit var mapViewContainer: ViewGroup
    lateinit var mapView: MapView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View
    {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_map, container, false)
        viewModel = ViewModelProvider(this).get(ListViewModel::class.java)
        binding.lifecycleOwner = this
        binding.map = viewModel

        x = arguments?.getDouble("x")
        y = arguments?.getDouble("y")
        placeName = arguments?.getString("placeName")
        click = arguments?.getBoolean("click")

        kakaoMap = binding.mapView
        mapViewContainer = kakaoMap
        mapView = MapView(requireActivity())
        mapViewContainer.addView(mapView)

        viewModel.setMarkers(requireContext(), this)

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        mapView.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()
        mapView.visibility = View.INVISIBLE
//        mapViewContainer.removeView(mapView)
    }



    fun setPosition(y: Double, x: Double) {
        val mapPoint = MapPoint.mapPointWithGeoCoord(y, x)

        mapView.setMapCenterPointAndZoomLevel(mapPoint, mapView.zoomLevel ?: 1, true)

    }


    fun addMarkers(name: String, y: Double, x: Double) {
        val point = MapPOIItem()
        point.apply {
            itemName = name
            mapPoint = MapPoint.mapPointWithGeoCoord(y,x)
            customImageResourceId = R.drawable.main_color1_marker
            customSelectedImageResourceId = R.drawable.main_color2_marker
            markerType = MapPOIItem.MarkerType.CustomImage
            selectedMarkerType = MapPOIItem.MarkerType.CustomImage
            isCustomImageAutoscale = false
        }
        mapView.addPOIItem(point)
    }


    fun setMyLocation() {
        //허가 받고 처음 페이지는 내위치에 띄움
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

            }catch (e: NullPointerException){
                Log.e("LOCATION_ERROR", e.toString())
                ActivityCompat.finishAffinity(requireActivity())
            }
        }else{
            Toast.makeText(requireActivity(), "위치 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }


}

