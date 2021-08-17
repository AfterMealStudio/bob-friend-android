package com.example.bob_friend_android.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.BoardData
import com.example.bob_friend_android.MyBoardAdapter
import com.example.bob_friend_android.R


class ListFragment : Fragment() {
    private val boardList : ArrayList<BoardData> = ArrayList()
    lateinit var boardRecyclerView : RecyclerView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        boardList.add(BoardData("저녁먹을사람",
            "지코바","오희주",
            0,10, 0,
       "2021-08-17"
        ))
        boardList.add(BoardData("저녁먹을사람",
            "지코바","오희주",
            0,10, 0,
            "2021-08-17"
        ))
        boardList.add(BoardData("저녁먹을사람",
            "지코바","오희주",
            0,10, 0,
            "2021-08-17"
        ))
        boardList.add(BoardData("저녁먹을사람",
            "지코바","오희주",
            0,10, 0,
            "2021-08-17"
        ))

        boardRecyclerView = view.findViewById(R.id.recyclerview) as RecyclerView
        boardRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        boardRecyclerView.adapter = MyBoardAdapter(requireActivity(), boardList)
        return view
    }
}