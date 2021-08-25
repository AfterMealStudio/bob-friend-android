package com.example.bob_friend_android.Activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.Adapter.BoardAdapter
import com.example.bob_friend_android.Adapter.CommentAdapter
import com.example.bob_friend_android.DataModel.Board
import com.example.bob_friend_android.DataModel.Comment
import com.example.bob_friend_android.databinding.ActivityReadBoardBinding
import java.util.*

class ReadBoardActivity : AppCompatActivity() {

    private lateinit var binding : ActivityReadBoardBinding
    private val commentList : ArrayList<Comment> = ArrayList()
    lateinit var datas : Board

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReadBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        commentList.add(Comment(123123,1231231,null,"ddd","dddd", "2021-08-08",null,null, 0))

        val adpater = CommentAdapter(commentList)
        binding.commentRecyclerview.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.commentRecyclerview.adapter = adpater
    }
}