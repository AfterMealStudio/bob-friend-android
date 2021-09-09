package com.example.bob_friend_android.view

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.adapter.CommentAdapter
import com.example.bob_friend_android.model.Comment
import com.example.bob_friend_android.databinding.ActivityDetailBoardBinding
import com.example.bob_friend_android.model.BoardItem
import com.example.bob_friend_android.viewmodel.BoardViewModel
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.util.*

class DetailBoardActivity : AppCompatActivity() {
    private val TAG = "DetailBoardActivity"
    private lateinit var binding: ActivityDetailBoardBinding
    private lateinit var viewModel: BoardViewModel
    private val commentList : ArrayList<Comment> = ArrayList()
    var backKeyPressedTime: Long = 0

    lateinit var mapView: MapView
    lateinit var mapViewContainer: RelativeLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_board)
        viewModel = ViewModelProvider(this).get(BoardViewModel::class.java)
        binding.lifecycleOwner = this
        binding.detail = viewModel

        mapView = MapView(this)
        mapViewContainer = binding.detailMapView
        mapViewContainer.addView(mapView)
        mapViewContainer.setBackgroundColor(Color.BLUE)

        Log.d(TAG, "detail-map: $mapView")

        if(intent.hasExtra("item")) {
            val item = intent.getParcelableExtra<BoardItem>("item")
            if(item != null) {
                binding.detailTitle.text = item.title.toString()
                binding.detailContent.text = item.content
//                binding.detailWriter.text = item.username
//                binding.detailWriteTime.text = item.createdAt
                binding.detailCurrentMember.text = item.currentNumberOfPeople.toString()
                binding.detailTotalMember.text = item.totalNumberOfPeople.toString()
                binding.detailAppointmentPlaceName.text = item.location

                val marker = MapPOIItem()
                marker.apply {
                    itemName = item.location
                    mapPoint = MapPoint.mapPointWithGeoCoord(item.y, item.x)
                    customImageResourceId = R.drawable.main_color1_marker
                    customSelectedImageResourceId = R.drawable.main_color2_marker
                    markerType = MapPOIItem.MarkerType.CustomImage
                    selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                    isCustomImageAutoscale = false
                    setCustomImageAnchor(0.5f, 1.0f)
                    mapView.setMapCenterPointAndZoomLevel(mapPoint, mapView.zoomLevel, true)
                }
                mapView.addPOIItem(marker)
            }
        }

        val adpater = CommentAdapter(commentList)
        binding.commentRecyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.commentRecyclerview.adapter = adpater
        commentList.add(Comment(123123,1231231,null,"ddd","dddd", "2021-08-08",null,null, 0))
        commentList.add(Comment(123123,1231231,null,"ddd","dddd", "2021-08-08",null,null, 0))
        commentList.add(Comment(123123,1231231,null,"ddd","dddd", "2021-08-08",null,null, 0))
        commentList.add(Comment(123123,1231231,null,"ddd","dddd", "2021-08-08",null,null, 0))
        commentList.add(Comment(123123,1231231,null,"ddd","dddd", "2021-08-08",null,null, 0))
        commentList.add(Comment(123123,1231231,null,"ddd","dddd", "2021-08-08",null,null, 0))
        commentList.add(Comment(123123,1231231,null,"ddd","dddd", "2021-08-08",null,null, 0))
        commentList.add(Comment(123123,1231231,null,"ddd","dddd", "2021-08-08",null,null, 0))

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onStop() {
        super.onStop()

        binding.detailMapView.removeView(mapView)
    }
}