package com.example.bob_friend_android.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bob_friend_android.adapter.CommentAdapter
import com.example.bob_friend_android.adapter.UserAdapter
import com.example.bob_friend_android.model.Board
import com.example.bob_friend_android.model.Comment
import com.example.bob_friend_android.model.User
import com.example.bob_friend_android.network.RetrofitBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardViewModel(application: Application): AndroidViewModel(application) {

    val TAG = "CreateBoardViewModel"

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading : LiveData<Boolean>
        get() = _isLoading

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


    fun readBoard(context: Context, recruitmentId: Int){
        RetrofitBuilder.api.getRecruitment(recruitmentId).enqueue(object : Callback<Board> {
            override fun onResponse(call: Call<Board>, response: Response<Board>) {
                if(response.body() != null) {
                    val board = Board()
                    board.id = response.body()!!.id
                    board.title = response.body()!!.title
                    board.content = response.body()!!.content
                    board.members = response.body()!!.members
                    board.author = response.body()!!.author
                    board.totalNumberOfPeople = response.body()!!.totalNumberOfPeople
                    board.restaurantName = response.body()!!.restaurantName
                    board.restaurantAddress = response.body()!!.restaurantAddress
                    board.latitude = response.body()!!.latitude
                    board.longitude = response.body()!!.longitude
                    board.appointmentTime = response.body()!!.appointmentTime
                    board.currentNumberOfPeople = response.body()!!.currentNumberOfPeople
                    board.full = response.body()!!.full
                    board.createdAt = response.body()!!.createdAt
                    board.report = response.body()!!.report
                    board.amountOfComments = response.body()!!.amountOfComments
                }
            }

            override fun onFailure(call: Call<Board>, t: Throwable) {
                Toast.makeText(context, "서버에 연결이 되지 않았습니다. 다시 시도해주세요!", Toast.LENGTH_SHORT).show()
                Log.e(TAG, t.message.toString())
            }
        })
    }

    fun participateBoard(userAdapter: UserAdapter, context: Context, recruitmentId: Int){
        Log.d("users!!!!!!!!!!!!!!!!!!!!!!!!!", "partici")
        val list: ArrayList<User> = arrayListOf()
        RetrofitBuilder.api.participateBoard(recruitmentId).enqueue(object : Callback<Board> {
            override fun onResponse(call: Call<Board>, response: Response<Board>) {
                Log.d("users!!!!!!!!!!!!!!!!!!!!!!!!!", response.body().toString())
                if(response.body() != null) {
                    for (document in response.body()!!.members!!) {
                        Log.d("users!!!!!!!!!!!!!!!!!!!!!!!!!", response.body().toString())
                        val user = User(nickname = document.nickname, id = 0, email = "test1", birth = "0716", sex = "None", activated = true, agree = true, reportCount = 0, accumulatedReports = 0)
                        list.add(user)
                    }
                }
                userAdapter.addCommentItems(list)
            }

            override fun onFailure(call: Call<Board>, t: Throwable) {
                Toast.makeText(context, "서버에 연결이 되지 않았습니다. 다시 시도해주세요!", Toast.LENGTH_SHORT).show()
                Log.e("users!!!!!!!!!!!!!!!!!!!!!!!!!", t.message.toString())
            }
        })
    }

    fun setComments(commentAdapter: CommentAdapter, recruitmentId: Int, context: Context){
        val list: ArrayList<Comment> = arrayListOf()
        RetrofitBuilder.api.getComments(recruitmentId).enqueue(object : Callback<List<Comment>> {
            override fun onResponse(call: Call<List<Comment>>, response: Response<List<Comment>>) {
                if(response.body() != null) {
                    for (document in response.body()!!) {
                        Log.d("comments!!!!!!!!!!!!!!!!!!!!!!!!!", response.body().toString())
                        val comment = Comment(id = document.id, author = document.author, content = document.content, replies = document.replies, typeFlag = 0, createdAt = document.createdAt)
                        list.add(comment)

                        if(document.replies !== null) {
                            for (recomments in document.replies!!){
                                val recomment = Comment(recomments.id, recomments.author, recomments.content, recomments.replies, typeFlag = 1, createdAt = document.createdAt)
                                list.add(recomment)
                            }
                        }
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


    fun addComment(commentAdapter: CommentAdapter, recruitmentId: Int, comment: String, context: Context){
        val commentData = HashMap<String, String>()
        commentData["content"] = comment

        RetrofitBuilder.api.addComment(recruitmentId, commentData).enqueue(object :Callback<Comment>{
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                val code = response.code()
                Log.d("addComment", "어 댓글 작성 성공??$code")
                if (code == 200) {
                    Log.d("addComment", "!!content=$comment")
                    Toast.makeText(context, "저장되었습니다!", Toast.LENGTH_SHORT).show()
                    setComments(commentAdapter, recruitmentId, context)
                }
            }

            override fun onFailure(call: Call<Comment>, t: Throwable) {
                Toast.makeText(context, "전송에 실패했습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun deleteComment(recruitmentId: Int, commentId:Int, context: Context){
        RetrofitBuilder.api.deleteComment(recruitmentId, commentId).enqueue(object :Callback<Void>{
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                val code = response.code()
                Log.d("delComment", "어 댓글 삭제 성공??$code")
                if (code == 200) {
                    Log.d("addComment", "!!content")
                    Toast.makeText(context, "저장되었습니다!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
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