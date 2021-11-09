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
import com.example.bob_friend_android.databinding.ItemBoardRecommmentsBinding
import com.example.bob_friend_android.model.Board

class CommentAdapter(private var list: MutableList<Comment>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val COMMENT_TYPE = 0
    private val RECOMMENT_TYPE = 1
    var writeTime = ""

    inner class CommentsViewHolder(private val binding: ItemBoardCommentsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Comment, context: Context) {
            binding.commentUserName.text = data.author?.nickname
            binding.commentContents.text = data.content
            if (data.createdAt != null) {
                val createDay: String = data.createdAt!!
                val created = createDay.split("T")

                writeTime = created[0] + " " +created[1].substring(0,5)
            }
            binding.commentTimestamp.text = writeTime
//            Glide.with(itemView).load(data.profileImg).into(profileImg)
            Log.d("CommentAdapter", data.toString())
        }
    }

    inner class RecommentsViewHolder(private val binding: ItemBoardRecommmentsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Comment, context: Context) {
            binding.recommentTextProfile.text = data.author?.nickname
            binding.recommentContents.text = data.content
            if (data.createdAt != null) {
                val createDay: String = data.createdAt!!
                val created = createDay.split("T")

                writeTime = created[0] + " " +created[1].substring(0,5)
            }
            binding.recommentTimestamp.text = writeTime
//            Glide.with(itemView).load(data.profileImg).into(profileImg)
            Log.d("CommentAdapter", data.toString())
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            COMMENT_TYPE -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBoardCommentsBinding.inflate(layoutInflater, parent, false)
                CommentsViewHolder(binding)
            }
            RECOMMENT_TYPE -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBoardRecommmentsBinding.inflate(layoutInflater, parent, false)
                RecommentsViewHolder(binding)
            }
            else -> throw RuntimeException("알 수 없는 뷰 타입 에러")
        }
    }

    override fun getItemViewType(position: Int): Int {
        when(list[position].typeFlag){
            0 -> {
                return COMMENT_TYPE
            }
            1 -> {
                return RECOMMENT_TYPE
            }
            else -> {
                return COMMENT_TYPE
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is CommentsViewHolder -> {
                holder.bind(list[position], holder.itemView.context)
            }
            is RecommentsViewHolder -> {
                holder.bind(list[position], holder.itemView.context)
            }
        }
    }

    fun addCommentItems(item: List<Comment>) {
        list.clear()
        list.addAll(item)
        notifyDataSetChanged()
    }
}