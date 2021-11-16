package com.example.bob_friend_android.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bob_friend_android.adapter.CommentAdapter
import com.example.bob_friend_android.adapter.UserAdapter
import com.example.bob_friend_android.model.Board
import com.example.bob_friend_android.model.Comment
import com.example.bob_friend_android.model.User
import com.example.bob_friend_android.model.UserItem
import com.example.bob_friend_android.network.RetrofitBuilder
import com.kakao.network.response.ResponseData
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardViewModel(application: Application): AndroidViewModel(application) {

    val TAG = "CreateBoardViewModel"

    private val _result = MutableLiveData<Board>()
    val result : LiveData<Board>
        get() = _result

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

            RetrofitBuilder.api.addRecruitments(board).enqueue(object : Callback<Board> {
                override fun onResponse(call: Call<Board>, response: Response<Board>) {
                    val code = response.code()
                    if (code == 200) {
//                        _result.postValue(true)
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
        RetrofitBuilder.api.deleteRecruitment(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d(TAG, "response : $response")
                Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "서버에 연결이 되지 않았습니다. 다시 시도해주세요!", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun reportBoard(context: Context, id : Int) {
        RetrofitBuilder.api.reportBoard(id).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Toast.makeText(context, "신고되었습니다.", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "서버에 연결이 되지 않았습니다. 다시 시도해주세요!", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun reportComment(context: Context, recruitmentId : Int, commentId: Int) {
        RetrofitBuilder.api.reportComment(recruitmentId, commentId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Toast.makeText(context, "신고되었습니다.", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "서버에 연결이 되지 않았습니다. 다시 시도해주세요!", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun readBoard(context: Context, recruitmentId: Int){
        viewModelScope.launch {
            RetrofitBuilder.api.getRecruitment(recruitmentId).enqueue(object : Callback<Board> {
                override fun onResponse(call: Call<Board>, response: Response<Board>) {
                    if(response.body() != null) {
                        _result.postValue(response.body())
                    }
                }

                override fun onFailure(call: Call<Board>, t: Throwable) {
                    Toast.makeText(context, "서버에 연결이 되지 않았습니다. 다시 시도해주세요!", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, t.message.toString())
                }
            })
        }
    }

    fun participateBoard(userAdapter: UserAdapter, context: Context, recruitmentId: Int){
        val list: ArrayList<UserItem> = arrayListOf()
        viewModelScope.launch {
            RetrofitBuilder.api.participateBoard(recruitmentId).enqueue(object : Callback<Board> {
                override fun onResponse(call: Call<Board>, response: Response<Board>) {
                    if(response.body() != null) {
                        _result.postValue(response.body())
                    }
                }

                override fun onFailure(call: Call<Board>, t: Throwable) {
                    Toast.makeText(context, "서버에 연결이 되지 않았습니다. 다시 시도해주세요!", Toast.LENGTH_SHORT).show()
                    Log.e(TAG, t.message.toString())
                }
            })
        }
    }


    fun addComment(recruitmentId: Int, comment: String, context: Context){
        val commentData = HashMap<String, String>()
        commentData["content"] = comment

        RetrofitBuilder.api.addComment(recruitmentId, commentData).enqueue(object :Callback<Comment>{
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                val code = response.code()
                Log.d("addComment", "어 댓글 작성 성공??$code")
                if (code == 200) {
                    Log.d("addComment", "!!content=$comment")
                    Toast.makeText(context, "저장되었습니다!", Toast.LENGTH_SHORT).show()
                    readBoard(context, recruitmentId)
                }
            }

            override fun onFailure(call: Call<Comment>, t: Throwable) {
                Toast.makeText(context, "전송에 실패했습니다.", Toast.LENGTH_SHORT).show()
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