package com.example.bob_friend_android.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.databinding.ItemUserBinding
import com.example.bob_friend_android.model.Comment
import com.example.bob_friend_android.model.User

class UserAdapter(private var list: MutableList<User>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class UserViewHolder(private val binding: ItemUserBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: User, context: Context) {
            binding.userName.text = data.nickname
//            Glide.with(itemView).load(data.profileImg).into(profileImg)
            Log.d("UserAdapter", data.toString())
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            Comment.COMMENT_TYPE -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemUserBinding.inflate(layoutInflater, parent, false)
                UserViewHolder(binding)
            }
            Comment.RECOMMENT_TYPE -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemUserBinding.inflate(layoutInflater, parent, false)
                UserViewHolder(binding)
            }
            else -> throw RuntimeException("알 수 없는 뷰 타입 에러")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is UserAdapter.UserViewHolder){
            holder.bind(list[position], holder.itemView.context)
        }
    }
}