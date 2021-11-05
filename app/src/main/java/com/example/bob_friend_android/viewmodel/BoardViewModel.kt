package com.example.bob_friend_android.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.AndroidViewModel
import com.example.bob_friend_android.App
import com.example.bob_friend_android.adapter.CommentAdapter
import com.example.bob_friend_android.model.Board
import com.example.bob_friend_android.model.Comment
import com.example.bob_friend_android.network.RetrofitBuilder
import com.example.bob_friend_android.view.ListFragment
import com.example.bob_friend_android.view.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardViewModel(application: Application): AndroidViewModel(application) {

    val TAG = "CreateBoardViewModel"

    fun createBoard(title : String, content: String, count:String, address: String, locationName: String, x: Double?, y: Double?, time: String, gender: String, context: Context) {
        if(validation(title, content, count, address, locationName, x, y, time, gender, context)) {
            val board = Board(
                title = title,
                content = content,
                totalNumberOfPeople = count.toInt(),
                restaurantAddress = address,
                restaurantName = locationName,
                longitude = y,
                latitude = x,
                appointmentTime = time,
                sexRestriction = gender
            )

            Log.d(TAG, "!title=$title, content=$content")

            RetrofitBuilder.api.addRecruitmens(board).enqueue(object : Callback<Board> {
                override fun onResponse(call: Call<Board>, response: Response<Board>) {
                    val code = response.code()
                    if (code == 200) {
                        Log.d(TAG, "!!title=$title, content=$content")
                        Toast.makeText(context, "저장되었습니다!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Board>, t: Throwable) {
                    Toast.makeText(context, "서버에 연결이 되지 않았습니다. 다시 시도해주세요!", Toast.LENGTH_SHORT)
                        .show()
                    Log.e("AddViewModel!!!", t.message.toString())
                }
            })
        }
    }


    fun deleteBoard(context: Context, id : Int) {
        RetrofitBuilder.api.deleteRecruitmens(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "서버에 연결이 되지 않았습니다. 다시 시도해주세요!", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun setComments(commentAdapter: CommentAdapter, recruitmentId: Int, context: Context){
        val list: ArrayList<Comment> = arrayListOf()
        RetrofitBuilder.api.getComments(recruitmentId).enqueue(object : Callback<List<Comment>> {
            override fun onResponse(call: Call<List<Comment>>, response: Response<List<Comment>>) {
                if(response.body() != null) {
                    for (document in response.body()!!) {
                        val comment = Comment(document.commentId, document.userName, document.content, document.recomment)

                        list.add(comment)
                    }
                }
                commentAdapter.addCommentItems(list)
            }

            override fun onFailure(call: Call<List<Comment>>, t: Throwable) {
                Toast.makeText(context, "서버에 연결이 되지 않았습니다. 다시 시도해주세요!", Toast.LENGTH_SHORT).show()
                Log.e(TAG, t.message.toString())
            }
        })
    }


    fun validation(title : String, content: String, count:String, address: String, locationName: String, x: Double?, y: Double?, time: String, gender: String, context: Context): Boolean {
         if (title.isEmpty() || content.isEmpty()) {
             Toast.makeText(context, "약속 제목과 내용을 입력해주세요!", Toast.LENGTH_SHORT).show()
             return false
         }
         if (count == "0" || count.toString()=="") {
             Toast.makeText(context, "약속 인원의 수를 입력해주세요!", Toast.LENGTH_SHORT).show()
             return false
         }
         if (locationName.isEmpty()) {
             Toast.makeText(context, "약속 장소를 입력해주세요!", Toast.LENGTH_SHORT).show()
             return false
         }
         if (time.isEmpty()) {
             Toast.makeText(context, "약속 시간을 입력해주세요!", Toast.LENGTH_SHORT).show()
             return false
         }
         return true
    }
}