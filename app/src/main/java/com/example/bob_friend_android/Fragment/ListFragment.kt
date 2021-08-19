package com.example.bob_friend_android.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.Adapter.BoardAdapter
import com.example.bob_friend_android.DataModel.Board
import com.example.bob_friend_android.R
import java.util.*


class ListFragment : Fragment() {
    private val boardList : ArrayList<Board> = ArrayList()
    lateinit var boardRecyclerView : RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        boardList.add(Board(1233524,
            "지코바","오희주",
            "dddd",1, 10,
       0,"2021-08-19", report = null))
        boardList.add(Board(1233524,
            "지코바","오희주",
            "dddd",1, 10,
            0,"2021-08-19", report = null))
        boardList.add(Board(1233524,
            "지코바","오희주",
            "dddd",1, 10,
            0,"2021-08-19", report = null))
        boardList.add(Board(1233524,
            "지코바","오희주",
            "dddd",1, 10,
            0,"2021-08-19", report = null))
        boardList.add(Board(1233524,
            "지코바","오희주",
            "dddd",1, 10,
            0,"2021-08-19", report = null))

        boardRecyclerView = view.findViewById(R.id.recyclerview) as RecyclerView
        boardRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        boardRecyclerView.adapter = BoardAdapter(requireActivity(), boardList)
        return view
    }
}