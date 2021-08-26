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

class CommentAdapter(private var list: MutableList<Comment>): RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View?): RecyclerView.ViewHolder(itemView!!) {
        var profileImg: ImageView = itemView!!.findViewById(R.id.comment_image_profile)
        var userName: TextView = itemView!!.findViewById(R.id.comment_text_profile)
        var content: TextView = itemView!!.findViewById(R.id.comment_contents)
        var timestamp: TextView = itemView!!.findViewById(R.id.comment_timestamp)

        fun bind(data: Comment, context: Context) {
            userName.text = data.userName
            content.text = data.content
            timestamp.text = data.timestamp
            Glide.with(itemView).load(data.profileImg).into(profileImg)
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
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_board_comments, parent, false)
                ViewHolder(view)
            }
            Comment.RECOMMENT_TYPE -> {
                view = LayoutInflater.from(parent.context).inflate(R.layout.item_board_recommments, parent, false)
                ViewHolder(view)
            }
            else -> throw RuntimeException("알 수 없는 뷰 타입 에러")
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], holder.itemView.context)
    }
}