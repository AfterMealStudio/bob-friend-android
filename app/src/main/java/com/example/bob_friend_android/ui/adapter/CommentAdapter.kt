package com.example.bob_friend_android.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.databinding.ItemBoardCommentsBinding
import com.example.bob_friend_android.databinding.ItemBoardRecommmentsBinding
import com.example.bob_friend_android.model.Comment
import kotlinx.android.synthetic.main.item_board_comments.view.*
import kotlinx.android.synthetic.main.item_board_recommments.view.*

class CommentAdapter(private var list: MutableList<Comment>, boardId: Int): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val COMMENT_TYPE = 0
    private val RECOMMENT_TYPE = 1
    var writeTime = ""
    var board = boardId

    interface CommentClick
    {
        fun onCommentClick(view: View, position: Int, comment: Comment)
    }
    var commentClick: CommentClick? = null

    interface ReCommentClick
    {
        fun onReCommentClick(view: View, position: Int, commentId: Int, reComment: Comment)
    }
    var reCommentClick: ReCommentClick? = null

    inner class CommentsViewHolder(private val binding: ItemBoardCommentsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Comment, context: Context) {
            binding.commentUserName.text = data.author?.nickname
            binding.commentContents.text = data.content
            if (data.createdAt != null) {
                val createDay: String = data.createdAt!!
                val created = createDay.split("T")

                writeTime = created[0] + " " + created[1].substring(0, 5)
            }
            binding.commentTimestamp.text = writeTime
        }
    }

    inner class RecommentsViewHolder(private val binding: ItemBoardRecommmentsBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Comment, context: Context) {
            binding.recommentUserName.text = data.author?.nickname
            binding.recommentContents.text = data.content
            if (data.createdAt != null) {
                val createDay: String = data.createdAt!!
                val created = createDay.split("T")

                writeTime = created[0] + " " +created[1].substring(0,5)
            }
            binding.recommentTimestamp.text = writeTime
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
        return when(list[position].typeFlag){
            0 -> {
                COMMENT_TYPE
            }
            1 -> {
                RECOMMENT_TYPE
            }
            else -> {
                COMMENT_TYPE
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var commentId = 0
        when(holder){
            is CommentsViewHolder -> {
                holder.bind(list[position], holder.itemView.context)
                holder.itemView.comment_menu_btn.setOnClickListener {
                    commentClick?.onCommentClick(it, position, list[position])
                }
                commentId = list[position].id
            }
            is RecommentsViewHolder -> {
                holder.bind(list[position], holder.itemView.context)
                holder.itemView.recomment_menu_btn.setOnClickListener {
                    reCommentClick?.onReCommentClick(it, position, commentId, list[position])
                }
            }
        }
    }
}