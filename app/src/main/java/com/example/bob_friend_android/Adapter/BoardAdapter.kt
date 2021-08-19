package com.example.bob_friend_android.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.DataModel.Board
import com.example.bob_friend_android.R
import com.example.bob_friend_android.ReadBoardActivity
import java.util.*

class BoardAdapter(private val context: Context, private val boardList : ArrayList<Board>) : RecyclerView.Adapter<BoardAdapter.ViewHolder>() {
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

        fun bind(item: Board) {
            boardTitle.text = item.boardTitle
            boardContent.text = item.boardContent
            userName.text = item.userName
            currentNumberOfParticipants.text = item.currentNumberOfParticipants.toString()
            totalNumberOfParticipants.text = item.totalNumberOfParticipants.toString()
            currentNumberOfComments.text = item.currentNumberOfComments.toString()
            createDate.text = item.createDate


            itemView.setOnClickListener {
                Intent(context, ReadBoardActivity::class.java)
            }
//            itemView.setOnClickListener {
//                Intent(context, ProfileDetailActivity::class.java).apply {
//                    putExtra("data", item)
//                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                }.run { context.startActivity(this) }
//            }
        }
    }
}