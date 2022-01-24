package com.example.bob_friend_android.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.data.entity.Board
import com.example.bob_friend_android.databinding.ItemBoardBinding
import com.example.bob_friend_android.databinding.ItemLoadingBinding

class BoardAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0

    private val boardList = arrayListOf<Board>()

    inner class BoardViewHolder(private val binding: ItemBoardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Board) {
            binding.tvBoardTitle.text = item.title
            binding.tvBoardWriter.text = item.author?.nickname
            binding.tvCurrentMember.text = item.currentNumberOfPeople.toString()
            binding.tvTotalMember.text = item.totalNumberOfPeople.toString()
            binding.tvCreateDate.text = item.createdAt.toString()
            binding.tvCurrentCommentCount.text = item.amountOfComments.toString()

            val pos = absoluteAdapterPosition
            if(pos!= RecyclerView.NO_POSITION)
            {
                itemView.setOnClickListener {
                    listener?.onItemClick(itemView,item,pos)
                }
            }
        }
    }

    inner class LoadingViewHolder(private val binding: ItemLoadingBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemBoardBinding.inflate(layoutInflater, parent, false)
                BoardViewHolder(binding)
            }
            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemLoadingBinding.inflate(layoutInflater, parent, false)
                LoadingViewHolder(binding)
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(v:View, data: Board, pos : Int)
    }

    private var listener : OnItemClickListener? = null
    fun setOnItemClickListener(listener : OnItemClickListener) {
        this.listener = listener
    }

    override fun getItemCount(): Int = boardList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is BoardViewHolder){
            holder.bind(boardList[position])
        }
    }

    fun addItems(item: List<Board>) {
        boardList.clear()
        boardList.addAll(item)
        notifyDataSetChanged()
    }
}