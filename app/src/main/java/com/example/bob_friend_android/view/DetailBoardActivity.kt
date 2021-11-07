package com.example.bob_friend_android.view

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.adapter.CommentAdapter
import com.example.bob_friend_android.adapter.BoardAdapter
import com.example.bob_friend_android.adapter.CommentAdapter
import com.example.bob_friend_android.adapter.UserAdapter
import com.example.bob_friend_android.model.Comment
import com.example.bob_friend_android.databinding.ActivityDetailBoardBinding
import com.example.bob_friend_android.model.BoardItem
import com.example.bob_friend_android.model.User
import com.example.bob_friend_android.viewmodel.BoardViewModel
import com.example.bob_friend_android.viewmodel.ListViewModel
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log

class DetailBoardActivity : AppCompatActivity() {
    private val TAG = "DetailBoardActivity"
    private lateinit var binding: ActivityDetailBoardBinding
    private lateinit var viewModel: BoardViewModel
    private val commentList : ArrayList<Comment> = ArrayList()
    private val userList : ArrayList<User> = ArrayList()
    var backKeyPressedTime: Long = 0

    lateinit var mapView: MapView
    lateinit var mapViewContainer: RelativeLayout

    private lateinit var boarditem : BoardItem
//    private lateinit var boarditem : Int

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

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        Log.d(TAG, "detail-map: $mapView")

        if(intent.hasExtra("item")) {
            val item = intent.getParcelableExtra<BoardItem>("item")
            boarditem = intent.getParcelableExtra<BoardItem>("item")!!
//            boarditem = intent.getIntExtra("item", 0)

//            viewModel.setBoard(this, boarditem)

            binding.detailTitle.text = boarditem.title.toString()
            binding.detailContent.text = boarditem.content
//                binding.detailWriter.text = boarditem.username
//                binding.detailWriteTime.text = boarditem.createdAt
            binding.detailCurrentMember.text = boarditem.currentNumberOfPeople.toString()
            binding.detailTotalMember.text = boarditem.totalNumberOfPeople.toString()
            binding.detailAppointmentPlaceName.text = boarditem.location
            binding.detailCurrentComment.text = boarditem.currentNumberOfPeople.toString()

            val marker = MapPOIItem()
            marker.apply {
                itemName = boarditem.location
                mapPoint = MapPoint.mapPointWithGeoCoord(boarditem.y, boarditem.x)
                customImageResourceId = R.drawable.main_color1_marker
                customSelectedImageResourceId = R.drawable.main_color2_marker
                markerType = MapPOIItem.MarkerType.CustomImage
                selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                isCustomImageAutoscale = false
                setCustomImageAnchor(0.5f, 1.0f)
                mapView.setMapCenterPointAndZoomLevel(mapPoint, mapView.zoomLevel, true)
            }
            mapView.addPOIItem(marker)

            val commentAdpater = CommentAdapter(commentList)
            binding.commentRecyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            binding.commentRecyclerview.adapter = commentAdpater
            viewModel.setComments(binding.commentRecyclerview.adapter as CommentAdapter, boarditem.id, this)
        }

        val userAdapter = UserAdapter(userList)
        binding.detailMember.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.detailMember.adapter = userAdapter
        userList.add(User(1,"testMember@test.com", "testMember", "2021-10-26", "FEMALE", null, null, true, true))
        userList.add(User(1,"testMember@test.com", "testMember", "2021-10-26", "FEMALE", null, null, true, true))
        userList.add(User(1,"testMember@test.com", "testMember", "2021-10-26", "FEMALE", null, null, true, true))

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onStop() {
        super.onStop()

        binding.detailMapView.removeView(mapView)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.report_board
            -> Toast.makeText(this,"report board",Toast.LENGTH_SHORT).show()
            R.id.delete_board
            -> {
                viewModel.deleteBoard(this, boarditem.id)
                Log.d(TAG, "board delete!!!!!!!!!!!!!!!!!!!!!!!!! ${boarditem.id}")
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}