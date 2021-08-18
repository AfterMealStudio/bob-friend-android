package com.example.bob_friend_android

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyBoardAdapter(private val context: Context, private val boardList : ArrayList<BoardData>) : RecyclerView.Adapter<MyBoardAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_board,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = boardList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(boardList[position])
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private val boardTitle: TextView = itemView.findViewById(R.id.boardTitle)
        private val boardContent: TextView = itemView.findViewById(R.id.boardContent)
        private val userName: TextView = itemView.findViewById(R.id.writer)
        private val currentNumberOfParticipants: TextView = itemView.findViewById(R.id.currentNumberOfParticipants)
        private val totalNumberOfParticipants: TextView = itemView.findViewById(R.id.totalNumberOfParticipants)
        private val currentNumberOfComments: TextView = itemView.findViewById(R.id.currentNumberOfComments)
        private val createDate: TextView = itemView.findViewById(R.id.createDate)

        fun bind(item: BoardData) {
            boardTitle.text = item.boardTitle
            boardContent.text = item.boardContent
            userName.text = item.userName
            currentNumberOfParticipants.text = item.currentNumberOfParticipants.toString()
            totalNumberOfParticipants.text = item.totalNumberOfParticipants.toString()
            currentNumberOfComments.text = item.currentNumberOfComments.toString()
            createDate.text = item.createDate

            //Glide.with(itemView).load(item.img).into(imgProfile)
        }
    }
}