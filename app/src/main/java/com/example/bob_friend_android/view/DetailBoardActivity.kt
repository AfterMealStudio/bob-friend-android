package com.example.bob_friend_android.view

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.ViewCompat.canScrollVertically
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.App
import com.example.bob_friend_android.R
import com.example.bob_friend_android.adapter.CommentAdapter
import com.example.bob_friend_android.adapter.BoardAdapter
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
    lateinit var commentAdpater: CommentAdapter

    var boardId = 0

    lateinit var mapView: MapView
    lateinit var mapViewContainer: RelativeLayout

    private lateinit var boarditem : BoardItem
//    private lateinit var boarditem : Int

    val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        Toast.makeText(applicationContext,
            "취소", Toast.LENGTH_SHORT).show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val swipe = binding.swipeLayout
//        swipe.setOnRefreshListener {
//
//
//
//            swipe.isRefreshing = false
//        }

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
            binding.readWriter.text = boarditem.username
            var appointmentTime = ""
            if (boarditem.appointmentTime != null) {
                val createDay: String = boarditem.appointmentTime!!
                val created = createDay.split("T")
                appointmentTime = created[0] + ", " +created[1].substring(0,5)
            }
            binding.readMeetingTime2.text = appointmentTime
            binding.detailCurrentMember.text = boarditem.currentNumberOfPeople.toString()
            binding.detailTotalMember.text = boarditem.totalNumberOfPeople.toString()
            binding.detailAppointmentPlaceName.text = boarditem.location
            binding.detailCurrentComment.text = boarditem.accountOfComments.toString()

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

            commentAdpater = CommentAdapter(commentList)
            binding.commentRecyclerview.adapter = commentAdpater
            val commentLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            binding.commentRecyclerview.layoutManager = commentLayoutManager
            viewModel.setComments(commentAdpater, boarditem.id, this)

            binding.postComment.setOnClickListener {
                if (binding.editTextComment.text!=null){
                    viewModel.addComment(commentAdpater, boarditem.id, binding.editTextComment.text.toString(),this)
                }
            }

            val userAdapter = UserAdapter(userList)
            binding.detailMember.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            binding.detailMember.adapter = userAdapter

            if (boarditem.username == App.prefs.getString("nickname", "")){
                binding.detailButton.text = "마감하기"
            }
            else {
                binding.detailButton.setOnClickListener {
                    viewModel.participateBoard(userAdapter, this, boarditem.id)
                }
            }
        }

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

    fun withCommentItems(view: View) {
        val items = arrayOf("댓글쓰기", "신고하기", "삭제하기")
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setTitle("댓글 메뉴")
            setItems(items) { dialog, which ->
                when(items[which]) {
                    items[0] -> {
                        Toast.makeText(applicationContext, items[which] + " is clicked", Toast.LENGTH_SHORT).show()
                        viewModel.addComment(commentAdpater, boarditem.id, binding.editTextComment.text.toString(),this@DetailBoardActivity)
                    }
                    items[1] -> {
//                        viewModel.deleteComment()
                        Toast.makeText(applicationContext, items[which] + " is clicked", Toast.LENGTH_SHORT).show()
                    }
                    items[2] -> {
//                        viewModel.deleteComment()
                        Toast.makeText(applicationContext, items[which] + " is clicked", Toast.LENGTH_SHORT).show()
//                        viewModel.deleteComment(boarditem.id, ,this@DetailBoardActivity)
                    }
                }
            }

            setPositiveButton("취소", positiveButtonClick)
            show()
        }
    }

    fun withRecommentItems(view: View) {
        val items = arrayOf("신고하기", "삭제하기")
        val builder = AlertDialog.Builder(this)
        with(builder)
        {
            setTitle("댓글 메뉴")
            setItems(items) { dialog, which ->
                when(items[which]) {
                    items[0] -> {
                        Toast.makeText(applicationContext, items[which] + " is clicked", Toast.LENGTH_SHORT).show()
                    }
                    items[1] -> {
//                        viewModel.deleteComment()
                        Toast.makeText(applicationContext, items[which] + " is clicked", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            setPositiveButton("취소", positiveButtonClick)
            show()
        }
    }
}