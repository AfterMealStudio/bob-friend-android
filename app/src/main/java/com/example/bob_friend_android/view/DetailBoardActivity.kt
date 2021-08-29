package com.example.bob_friend_android.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.R
import com.example.bob_friend_android.adapter.CommentAdapter
import com.example.bob_friend_android.model.Comment
import com.example.bob_friend_android.databinding.ActivityDetailBoardBinding
import com.example.bob_friend_android.viewmodel.DetailBoardViewModel
import net.daum.mf.map.api.MapView
import java.util.*

class DetailBoardActivity : AppCompatActivity() {
    private val TAG = "DetailBoardActivity"
    private lateinit var binding: ActivityDetailBoardBinding
    private lateinit var viewModel: DetailBoardViewModel
    private val commentList : ArrayList<Comment> = ArrayList()
    var backKeyPressedTime: Long = 0

    lateinit var mapView: MapView
    lateinit var mapViewContainer: RelativeLayout

    var address: String = "null"
    var name: String = "null"
    var y: String = "null"
    var x: String = "null"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_board)
        viewModel = ViewModelProvider(this).get(DetailBoardViewModel::class.java)
        binding.lifecycleOwner = this
        binding.detail = viewModel

        mapView = MapView(this)
        mapViewContainer = binding.detailMapView
        mapViewContainer.addView(mapView)
        Log.d(TAG, "onCreate: $mapView")

        val id = intent.getIntExtra("id", 0)
        val title = intent.getStringExtra("title")
        val content = intent.getStringExtra("content")
        val username = intent.getStringExtra("username")
        val createAt = intent.getStringExtra("createdAt")
        val currentNumberOfPeople = intent.getStringExtra("currentNumberOfPeople")
        val totalNumberOfPeople = intent.getStringExtra("totalNumberOfPeople")
        val location = intent.getStringExtra("location")

        binding.detailTitle.text = title
        binding.detailContent.text = content
        binding.detailWriter.text = username
        binding.detailWriteTime.text = createAt
        binding.detailCurrentMember.text = currentNumberOfPeople
        binding.detailTotalMember.text = totalNumberOfPeople
        binding.detailAppointmentPlaceName.text = location

        commentList.add(Comment(123123,1231231,null,"ddd","dddd", "2021-08-08",null,null, 0))
        commentList.add(Comment(123123,1231231,null,"ddd","dddd", "2021-08-08",null,null, 0))
        commentList.add(Comment(123123,1231231,null,"ddd","dddd", "2021-08-08",null,null, 0))
        commentList.add(Comment(123123,1231231,null,"ddd","dddd", "2021-08-08",null,null, 0))
        commentList.add(Comment(123123,1231231,null,"ddd","dddd", "2021-08-08",null,null, 0))
        commentList.add(Comment(123123,1231231,null,"ddd","dddd", "2021-08-08",null,null, 0))


//        val adpater = CommentAdapter(commentList)
//        binding.commentRecyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
//        binding.commentRecyclerview.adapter = adpater

        binding.backBtn.setOnClickListener {
            onBackPressed()
        }
    }

    override fun finish() {
        binding.detailMapView.visibility = View.GONE
        super.finish()
    }

    override fun onBackPressed() {
        finish()
        super.onBackPressed()
    }
}