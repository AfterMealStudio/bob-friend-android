package com.example.bob_friend_android.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.databinding.ItemUserBinding
import com.example.bob_friend_android.data.entity.Comment
import com.example.bob_friend_android.data.entity.UserItem

class UserAdapter(private var list: MutableList<UserItem>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class UserViewHolder(private val binding: ItemUserBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: UserItem, context: Context) {
            binding.tvUserName.text = data.nickname
            binding.rbUserRating.rating = data.rating.toFloat()
            binding.tvUserAge.text = data.age.toString()
            binding.tvUserGender.text = data.sex
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

    fun addCommentItems(item: List<UserItem>) {
        list.clear()
        list.addAll(item)
        notifyDataSetChanged()
    }
}