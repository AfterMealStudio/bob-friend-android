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
import androidx.core.view.get
import androidx.core.view.size
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
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
import com.example.bob_friend_android.databinding.DialogCommentBinding
import com.example.bob_friend_android.model.BoardItem
import com.example.bob_friend_android.model.User
import com.example.bob_friend_android.model.UserItem
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

    private var boardId : Int = 0
    private var userId: Int = 0

    private val commentList : ArrayList<Comment> = ArrayList()
    private val userList : ArrayList<UserItem> = ArrayList()
    private var commentAdapter = CommentAdapter(commentList, boardId)
    private val userAdapter = UserAdapter(userList)

    lateinit var mapView: MapView
    lateinit var mapViewContainer: RelativeLayout
    var appointmentTime = ""

    private val positiveButtonClick = { dialog: DialogInterface, which: Int ->
        Toast.makeText(applicationContext,
            "취소", Toast.LENGTH_SHORT).show()
    }

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

        commentAdapter.commentClick = object : CommentAdapter.CommentClick {
            override fun onCommentClick(view: View, position: Int, comment: Comment) {
                Toast.makeText(this@DetailBoardActivity, comment.toString(), Toast.LENGTH_SHORT).show()
                withCommentItems(comment.id, comment.author!!.id, true, null)
            }
        }

        commentAdapter.reCommentClick = object : CommentAdapter.ReCommentClick {
            override fun onReCommentClick(view: View, position: Int, commentId: Int, reComment: Comment) {
                Toast.makeText(this@DetailBoardActivity, reComment.toString(), Toast.LENGTH_SHORT).show()
                withCommentItems(commentId, reComment.author!!.id, false, reComment.id)
            }
        }

        viewModel.result.observe(this, Observer { board ->
            boardId = board.id
            binding.detailCurrentMember.text = board.currentNumberOfPeople.toString()
            binding.detailCurrentComment.text = board.amountOfComments.toString()
            binding.readWriter.text = board.author?.nickname

            binding.detailTitle.text = board.title
            binding.detailContent.text = board.content
            binding.readWriter.text = board.author!!.nickname
            if (board.appointmentTime != null) {
                val createDay: String = board.appointmentTime!!
                val created = createDay.split("T")
                appointmentTime = created[0] + ", " +created[1].substring(0,5)
            }
            binding.readMeetingTime2.text = appointmentTime
            binding.detailCurrentMember.text = board.currentNumberOfPeople.toString()
            binding.detailTotalMember.text = board.totalNumberOfPeople.toString()
            binding.detailAppointmentPlaceName.text = board.restaurantName
            binding.detailCurrentComment.text = board.amountOfComments.toString()

            val marker = MapPOIItem()
            marker.apply {
                itemName = board.restaurantName
                mapPoint = MapPoint.mapPointWithGeoCoord(board.latitude!!, board.longitude!!)
                customImageResourceId = R.drawable.main_color1_marker
                customSelectedImageResourceId = R.drawable.main_color2_marker
                markerType = MapPOIItem.MarkerType.CustomImage
                selectedMarkerType = MapPOIItem.MarkerType.CustomImage
                isCustomImageAutoscale = false
                setCustomImageAnchor(0.5f, 1.0f)
                mapView.setMapCenterPointAndZoomLevel(mapPoint, mapView.zoomLevel, true)
            }
            mapView.addPOIItem(marker)

//            commentAdapter = CommentAdapter(commentList)
            binding.commentRecyclerview.adapter = commentAdapter
            val commentLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            binding.commentRecyclerview.layoutManager = commentLayoutManager
//            viewModel.setComments(commentAdapter, board.id, this)

            binding.postComment.setOnClickListener {
                if (binding.editTextComment.text != null && binding.editTextComment.text.toString() != "") {
                    viewModel.addComment(
                        boardId,
                        binding.editTextComment.text.toString(),
                        this
                    )
                }
            }
            binding.detailMember.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            binding.detailMember.adapter = userAdapter

            if (board.author!!.id == App.prefs.getInt("id", -1)){
                binding.detailButton.text = "마감하기"
            }
            else {
                binding.detailButton.text = "참가하기"
                for (member in board.members!!){
                    if(member.id == App.prefs.getInt("id", -1)){
                        binding.detailButton.text = "취소하기"
                    }
                }
            }

            commentList.clear()
            if(board.comments != null) {
                for (comment in board.comments!!) {
                    commentList.add(comment)
                    if(comment.replies !== null) {
                        for (recomment in comment.replies!!){
                            val reComment = Comment(recomment.id, recomment.author, recomment.content, recomment.replies, typeFlag = 1, createdAt = recomment.createdAt)
                            commentList.add(reComment)
                        }
                    }
                }
            }

            userList.clear()
            for (member in board.members!!) {
                userList.add(member)
            }
//            userAdapter.notifyDataSetChanged()
        })

        if(intent.hasExtra("boardId")) {
            boardId = intent.getIntExtra("boardId", 0)
            userId = intent.getIntExtra("userId", 0)

            val swipe = binding.swipeLayout
            swipe.setOnRefreshListener {
                viewModel.readBoard(this, boardId)
                swipe.isRefreshing = false
            }

            binding.detailButton.setOnClickListener {
                viewModel.participateBoard(userAdapter, this, boardId)
            }

            viewModel.readBoard(this, boardId)
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
        if (userId == App.prefs.getInt("id", 0)){
            menu?.add(Menu.NONE, Menu.FIRST + 1, Menu.NONE, "삭제하기")
        }
        else {
            Log.d(TAG, "userId: $userId")
            menu?.add(Menu.NONE, Menu.FIRST + 2, Menu.NONE, "신고하기")
        }
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            Menu.FIRST + 1 -> {
                viewModel.deleteBoard(this, boardId)
                finish()
            }
            Menu.FIRST + 2 -> {
                Log.d(TAG, "boardId: $boardId")
                viewModel.reportBoard(this, boardId)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun withCommentItems(commentId: Int, commentWriterId: Int, Comment:Boolean, recommentId: Int?) {
        var writer = false

        if (commentWriterId == App.prefs.getInt("id", -1)){
            writer = true
        }

        val dialog = DialogCommentFragment(writer, Comment)
        // 버튼 클릭 이벤트 설정
        dialog.setButtonClickListener(object: DialogCommentFragment.OnButtonClickListener{
            override fun onAddReCommentClicked() {
                viewModel.addReComment(boardId, commentId, binding.editTextComment.text.toString(),this@DetailBoardActivity)
            }

            override fun onReportCommentClicked() {
                if (Comment){
                    viewModel.reportComment(boardId, commentId)
                }
                else {
                    if (recommentId != null) {
                        viewModel.reportReComment(boardId, commentId, recommentId)
                    }
                }
            }

            override fun onDeleteCommentClicked() {
                if (Comment){
                    viewModel.deleteComment(boardId, commentId)
                    viewModel.readBoard(this@DetailBoardActivity, boardId)
                }
                else {
                    if (recommentId != null) {
                        viewModel.deleteReComment(boardId, commentId, recommentId)
                        viewModel.readBoard(this@DetailBoardActivity, boardId)
                    }
                }
            }
        })
        dialog.show(supportFragmentManager, "CustomDialog")
    }
}