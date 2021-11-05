package com.example.bob_friend_android.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.bob_friend_android.model.Comment
import com.example.bob_friend_android.R
import com.example.bob_friend_android.databinding.ItemBoardBinding
import com.example.bob_friend_android.databinding.ItemBoardCommentsBinding
import com.example.bob_friend_android.model.Board

class CommentAdapter(private var list: MutableList<Comment>): RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemBoardCommentsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Comment, context: Context) {
            binding.commentUserName.text = data.userName
            binding.commentContents.text = data.content
            binding.commentTimestamp.text = data.timestamp
//            Glide.with(itemView).load(data.profileImg).into(profileImg)
            Log.d("CommentAdapter", data.toString())
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View?
        return when (viewType) {
            Comment.COMMENT_TYPE -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBoardCommentsBinding.inflate(layoutInflater, parent, false)
                ViewHolder(binding)
            }
            Comment.RECOMMENT_TYPE -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBoardCommentsBinding.inflate(layoutInflater, parent, false)
                ViewHolder(binding)
            }
            else -> throw RuntimeException("알 수 없는 뷰 타입 에러")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], holder.itemView.context)
    }

    fun addCommentItems(item: List<Comment>) {
        list.clear()
        list.addAll(item)
        notifyDataSetChanged()
    }
}