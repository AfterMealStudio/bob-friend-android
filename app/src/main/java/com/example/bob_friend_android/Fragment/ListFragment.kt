package com.example.bob_friend_android.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.adapter.BoardAdapter
import com.example.bob_friend_android.model.Board
import com.example.bob_friend_android.R
import java.util.*


class ListFragment : Fragment() {
    private val boardList : ArrayList<Board> = ArrayList()
    lateinit var boardRecyclerView : RecyclerView
    private lateinit var boardAdapter: BoardAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        boardRecyclerView = view.findViewById(R.id.recyclerview) as RecyclerView
        boardRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        boardRecyclerView.adapter = BoardAdapter(requireActivity(), boardList)

        return view
    }
}