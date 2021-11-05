package com.example.bob_friend_android.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.model.Board
import com.example.bob_friend_android.databinding.ItemBoardBinding
import com.example.bob_friend_android.databinding.ItemLoadingBinding
import com.example.bob_friend_android.model.BoardItem
import com.example.bob_friend_android.view.DetailBoardActivity
import java.util.*

class BoardAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_ITEM = 0
    lateinit var boardItem: BoardItem

    private val boardList = arrayListOf<Board>()

    inner class BoardViewHolder(private val binding: ItemBoardBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Board) {
            binding.boardTitle.text = item.title
            binding.boardWriter.text = item.author?.nickname
            binding.currentNumberOfParticipants.text = item.currentNumberOfPeople.toString()
            binding.totalNumberOfParticipants.text = item.totalNumberOfPeople.toString()
            binding.createDate.text = item.createdAt.toString()
            binding.currentNumberOfComments.text = item.amountOfComments.toString()

            val pos = absoluteAdapterPosition

            itemView.setOnClickListener {
                listener?.onItemClick(itemView, item, pos)
                Intent(context, DetailBoardActivity::class.java).apply {
                    boardItem = BoardItem(item.id, item.title, item.content, item.author?.nickname,
                        item.currentNumberOfPeople, item.totalNumberOfPeople!!, item.createdAt, item.restaurantName, item.latitude!!, item.longitude!!, item.amountOfComments)
                    putExtra("item", boardItem)
//                    putExtra("item", item.id)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }.run { context.startActivity(this) }
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