package com.example.bob_friend_android.viewmodel

import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.recyclerview.widget.RecyclerView
import com.example.bob_friend_android.R
import com.example.bob_friend_android.adapter.BoardAdapter
import com.example.bob_friend_android.adapter.SearchAdapter
import com.example.bob_friend_android.model.Board
import com.example.bob_friend_android.model.Locations
import com.example.bob_friend_android.model.SearchKeyword
import com.example.bob_friend_android.model.SearchLocation
import com.example.bob_friend_android.model.*
import com.example.bob_friend_android.network.KakaoAPI
import com.example.bob_friend_android.network.RetrofitBuilder
import com.example.bob_friend_android.view.MainActivity
import com.example.bob_friend_android.view.MapFragment
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ListViewModel(application: Application) : AndroidViewModel(application) {

    val TAG = "ListViewModel"

    fun setList(boardAdapter: BoardAdapter, context: Context, listPage: Int, list: ArrayList<Board>){
        var lastPage: Boolean
        var element: Int

        RetrofitBuilder.api.getRecruitmens(listPage).enqueue(object : Callback<BoardList> {
            override fun onResponse(call: Call<BoardList>, response: Response<BoardList>) {
                if(response.body() != null) {
                    element = response.body()!!.element
                    lastPage = response.body()!!.last
                    if((element == 20 && !lastPage)||(element != 0 && lastPage)){
                        for (document in response.body()!!.boardList) {
                            val board = Board()
                            board.id = document.id
                            board.title = document.title
                            board.content = document.content
                            board.member = document.member
                            board.author = document.author
                            board.totalNumberOfPeople = document.totalNumberOfPeople
                            board.restaurantName = document.restaurantName
                            board.restaurantAddress = document.restaurantAddress
                            board.latitude = document.latitude
                            board.longitude = document.longitude
                            board.appointmentTime = document.appointmentTime
                            board.currentNumberOfPeople = document.currentNumberOfPeople
                            board.full = document.full
                            board.createdAt = document.createdAt
                            board.report = document.report
                            board.amountOfComments = document.amountOfComments

                            list.add(board)
                        }
                        boardAdapter.addItems(list)
                    }
                }
            }

            override fun onFailure(call: Call<BoardList>, t: Throwable) {
                Toast.makeText(context, "서버에 연결이 되지 않았습니다. 다시 시도해주세요!", Toast.LENGTH_SHORT).show()
                Log.e(TAG, t.message.toString())
            }
        })
    }


    fun setMyAppointment(recyclerView: RecyclerView, context: Context) {
        val list: ArrayList<Board> = arrayListOf()
        RetrofitBuilder.api.getJoinRecruitmens().enqueue(object : Callback<List<Board>> {
            override fun onResponse(call: Call<List<Board>>, response: Response<List<Board>>) {
                if(response.body() != null) {
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
                        board.appointmentTime = document.appointmentTime
                        board.member = document.member
                        board.currentNumberOfPeople = document.currentNumberOfPeople
                        board.full = document.full
                        board.createdAt = document.createdAt
                        board.report = document.report

                        list.add(board)
                    }
                }
                else {
                    Toast.makeText(context, "내가 참가하는 약속이 없습니다.", Toast.LENGTH_SHORT).show()
                }

//                val adapter = BoardAdapter(context, list)
//                recyclerView.adapter = adapter
            }

            override fun onFailure(call: Call<List<Board>>, t: Throwable) {
                Toast.makeText(context, "서버에 연결이 되지 않았습니다. 다시 시도해주세요!", Toast.LENGTH_SHORT).show()
                Log.e(TAG, t.message.toString())
            }
        })
    }


    fun setMyBoard(recyclerView: RecyclerView, context: Context) {
        val list: ArrayList<Board> = arrayListOf()
        RetrofitBuilder.api.getMyRecruitmens().enqueue(object : Callback<List<Board>> {
            override fun onResponse(call: Call<List<Board>>, response: Response<List<Board>>) {

                if(response.body() != null) {
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
                        board.appointmentTime = document.appointmentTime
                        board.member = document.member
                        board.currentNumberOfPeople = document.currentNumberOfPeople
                        board.full = document.full
                        board.createdAt = document.createdAt
                        board.report = document.report

                        list.add(board)
                    }
                }
                else {
                    Toast.makeText(context, "내가 작성한 약속이 없습니다.", Toast.LENGTH_SHORT).show()
                }

//                val adapter = BoardAdapter(context, list)
//                recyclerView.adapter = adapter
            }

            override fun onFailure(call: Call<List<Board>>, t: Throwable) {
                Toast.makeText(context, "서버에 연결이 되지 않았습니다. 다시 시도해주세요!", Toast.LENGTH_SHORT).show()
                Log.e(TAG, t.message.toString())
            }
        })
    }

    fun setMarkers(context: Context, fragment: MapFragment) {
        val list: ArrayList<Locations> = arrayListOf()
        RetrofitBuilder.api.getRecruitmensLocations().enqueue(object : Callback<List<Locations>> {
            override fun onResponse(call: Call<List<Locations>>, response: Response<List<Locations>>) {

                if(response.body() != null) {
                    for (document in response.body()!!) {
                        val locations = Locations()
                        locations.latitude = document.latitude
                        locations.longitude = document.longitude
                        locations.address = document.address
                        list.add(locations)
                        for(marker in list){
                            fragment.addMarkers(document.address, document.longitude, document.latitude)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Locations>>, t: Throwable) {
                Toast.makeText(context, "서버에 연결이 되지 않았습니다. 다시 시도해주세요!", Toast.LENGTH_SHORT).show()
                Log.e(TAG, t.message.toString())
            }
        })
    }


    fun setDataAtFragment(fragment: Fragment, placeName:String, y:Double, x:Double) {
        val bundle = Bundle()
        bundle.putString("placeName", placeName)
        bundle.putDouble("x", x)
        bundle.putDouble("y", y)
        bundle.putBoolean("click", true)

        fragment.arguments = bundle
    }


    fun searchKeyword(keyword: String, searchAdapter: SearchAdapter, context: Context) {
        val retrofit = Retrofit.Builder()   // Retrofit 구성
            .baseUrl(MainActivity.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)   // 통신 인터페이스를 객체로 생성
        val call = api.getSearchKeyword(MainActivity.API_KEY, keyword)   // 검색 조건 입력

        // API 서버에 요청
        call.enqueue(object: Callback<SearchKeyword> {
            override fun onResponse(
                call: Call<SearchKeyword>,
                response: Response<SearchKeyword>
            ) {
                addItems(response.body(), context, searchAdapter)
            }

            override fun onFailure(call: Call<SearchKeyword>, t: Throwable) {
                // 통신 실패
                Log.w(TAG, "통신 실패: ${t.message}")
            }
        })
    }


    fun addItems(searchResult: SearchKeyword?, context:Context, adapter: SearchAdapter) {
        val listItems: ArrayList<SearchLocation> = adapter.getItems()

        if (!searchResult?.documents.isNullOrEmpty()) { // 검색 결과 있음
            listItems.clear()
            for (document in searchResult!!.documents) {
                // 결과를 리사이클러 뷰에 추가
                val item = SearchLocation(document.place_name,
                    document.road_address_name,
                    document.address_name,
                    document.x.toDouble(),
                    document.y.toDouble())
                listItems.add(item)}

            adapter.notifyDataSetChanged()
        } else { // 검색 결과 없음
            Toast.makeText(context, "검색 결과가 없습니다", Toast.LENGTH_SHORT).show()
        }
    }
}