package com.example.bob_friend_android.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.ListAdapter
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.App
import com.example.bob_friend_android.adapter.BoardAdapter
import com.example.bob_friend_android.model.Board
import com.example.bob_friend_android.network.RetrofitBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListViewModel(application: Application) : AndroidViewModel(application) {

    val TAG = "ListViewModel"

    fun setList(recyclerView: RecyclerView, context: Context) {

        val token = App.prefs.getString("token","")
        val list: ArrayList<Board> = arrayListOf()
        Log.d(TAG, "token = $token")

        if (token != null) {
            RetrofitBuilder.api.getRecruitmens(token).enqueue(object : Callback<List<Board>> {
                override fun onResponse(call: Call<List<Board>>, response: Response<List<Board>>) {
                    Log.d(TAG, "responese = ${response.body()}")
                    for (document in response.body()!!) {
                        val board = Board()
                        board.id = document.id
                        board.title = document.title
                        board.content = document.content
                        board.author = document.author
                        board.totalNumberOfPeople = document.totalNumberOfPeople
                        board.restaurantName = document.restaurantName
                        board.restaurantAddress = document.restaurantAddress
                        board.latitude = document.latitude
                        board.longitude = document.longitude
                        board.startAt = document.startAt
                        board.endAt = document.endAt
                        board.member = document.member
                        board.currentNumberOfPeople = document.currentNumberOfPeople
                        board.full = document.full
                        board.createdAt = document.createdAt
                        board.report = document.report

                        list.add(board)
                    }
                    val adapter = BoardAdapter(context, list)
                    recyclerView.adapter = adapter
                }

                override fun onFailure(call: Call<List<Board>>, t: Throwable) {
                    Toast.makeText(context, "서버에 연결이 되지 않았습니다. 다시 시도해주세요!", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, t.message.toString())
                }
            })
        }
    }
}