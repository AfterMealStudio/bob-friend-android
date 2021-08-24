package com.example.bob_friend_android.Fragment

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
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.bob_friend_android.R
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment() {
    private var x: Double? = null
    private var y: Double? = null
    private var placeName: String? = null
    private var click: Boolean? = false
    private lateinit var mapView:MapView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view : View = inflater.inflate(R.layout.fragment_map, container, false)

        mapView = MapView(requireActivity())
        val kakaoMap = view.findViewById<View>(R.id.map_view)
        val mapViewContainer = kakaoMap as ViewGroup

        setMyLocation()

        x = arguments?.getDouble("x")
        y = arguments?.getDouble("y")
        placeName = arguments?.getString("placeName")
        click = arguments?.getBoolean("click")

        if (click==true) { addMarkers(placeName!!, x!!, y!!) }

        Log.d("MapFragment", "arguments:${arguments} x:${x}, y:${y}, click:${click}")

        mapViewContainer.addView(mapView)

        return view
    }


    fun setPosition(y: Double, x: Double) {
        val mapPoint = MapPoint.mapPointWithGeoCoord(y, x)

        mapView.setMapCenterPointAndZoomLevel(mapPoint, mapView.zoomLevel, true)
    }


    fun addMarkers(name: String, y: Double, x: Double) {
        val point = MapPOIItem()
        point.apply {
            itemName = name
            mapPoint = MapPoint.mapPointWithGeoCoord(y,x)
            markerType = MapPOIItem.MarkerType.BluePin
            selectedMarkerType = MapPOIItem.MarkerType.RedPin
        }
        mapView.addPOIItem(point)
    }


    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        var ft: FragmentTransaction = fragmentManager.beginTransaction()
        ft.detach(fragment).attach(fragment).commit()
    }


    private fun setMyLocation() {
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
                val marker = MapPOIItem()
                marker.itemName = "내위치"
                marker.mapPoint = MapPoint.mapPointWithGeoCoord(uLatitude, uLongitude)
                marker.markerType = MapPOIItem.MarkerType.BluePin
                marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
                mapView.addPOIItem(marker)

            }catch (e: NullPointerException){
                Log.e("LOCATION_ERROR", e.toString())
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    ActivityCompat.finishAffinity(requireActivity())
                }else{
                    ActivityCompat.finishAffinity(requireActivity())
                }

                val intent = Intent(requireActivity(), MapFragment::class.java)
                startActivity(intent)
                System.exit(0)
            }
        }else{
            Toast.makeText(requireActivity(), "위치 권한이 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }
}

