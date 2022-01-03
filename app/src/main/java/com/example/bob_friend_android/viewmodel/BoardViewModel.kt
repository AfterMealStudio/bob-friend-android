package com.example.bob_friend_android.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bob_friend_android.model.Board
import com.example.bob_friend_android.model.Comment
import com.example.bob_friend_android.network.RetrofitBuilder
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardViewModel(application: Application): AndroidViewModel(application) {

    val TAG = "CreateBoardViewModel"

    private val _result = MutableLiveData<Board>()
    val result : LiveData<Board>
        get() = _result

    private val _msg = MutableLiveData<String>()
    val errorMsg : LiveData<String>
        get() = _msg

    private val _progressVisible = MutableLiveData<Boolean>()
    val progressVisible : LiveData<Boolean>
        get() = _progressVisible


    fun createBoard(title : String, content: String, count:String, address: String, locationName: String, x: Double?, y: Double?, time: String,
                    gender: String, ageRestrictionStart: Int?, ageRestrictionEnd: Int?) {
        if(validation(title, content, count, locationName, time)) {
            val board = Board(
                title = title,
                content = content,
                totalNumberOfPeople = count.toInt(),
                restaurantAddress = address,
                restaurantName = locationName,
                longitude = x,
                latitude = y,
                appointmentTime = time,
                sexRestriction = gender,
                ageRestrictionStart = ageRestrictionStart,
                ageRestrictionEnd = ageRestrictionEnd
            )

            _progressVisible.postValue(true)
            RetrofitBuilder.apiBob.addRecruitments(board).enqueue(object : Callback<Board> {
                override fun onResponse(call: Call<Board>, response: Response<Board>) {
                    val code = response.code()
                    Log.d(TAG, "createBoard: title=$title, content=$content, responseCode: ${response.code()}, error : ${response.errorBody().toString()}")
                    if (code == 200) {
                        _msg.postValue("약속이 작성되었습니다!")
                    }
                    _progressVisible.postValue(false)
                }

                override fun onFailure(call: Call<Board>, t: Throwable) {
                    _msg.postValue("서버에 연결이 되지 않았습니다. 다시 시도해주세요!")
                    Log.e("AddViewModel!!!", t.message.toString())
                }
            })
        }
    }


    fun readBoard(recruitmentId: Int){
        _progressVisible.postValue(true)
        viewModelScope.launch {
            RetrofitBuilder.apiBob.getRecruitment(recruitmentId).enqueue(object : Callback<Board> {
                override fun onResponse(call: Call<Board>, response: Response<Board>) {
                    Log.d(TAG, "readBoard : ${response.code()}")
                    if (response.code() == 200){
                        _result.postValue(response.body())
                    }
                    else if(response.code() == 403){
                        _msg.postValue("삭제되거나 마감된 글입니다.")
                    }
                    _progressVisible.postValue(false)
                }

                override fun onFailure(call: Call<Board>, t: Throwable) {
                    _msg.postValue("서버에 연결이 되지 않았습니다. 다시 시도해주세요!")
                    Log.e(TAG, t.message.toString())
                }
            })
        }
    }


    fun deleteBoard(recruitmentId : Int) {
        _progressVisible.postValue(true)
        RetrofitBuilder.apiBob.deleteRecruitment(recruitmentId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d(TAG, "deleteBoard : $response")
                _msg.postValue("약속이 삭제되었습니다.")
                _progressVisible.postValue(false)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다. 다시 시도해주세요!")
            }
        })
    }


    fun deleteComment(recruitmentId: Int, commentId : Int) {
        _progressVisible.postValue(true)
        RetrofitBuilder.apiBob.deleteComment(recruitmentId,commentId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d(TAG, "deleteComment : $response")
                _msg.postValue("댓글이 삭제되었습니다.")
                _progressVisible.postValue(false)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다. 다시 시도해주세요!")
            }
        })
    }


    fun deleteReComment(recruitmentId: Int, commentId : Int, recommentId : Int) {
        _progressVisible.postValue(true)
        RetrofitBuilder.apiBob.deleteReComment(recruitmentId,commentId,recommentId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d(TAG, "deleteReComment : $response")
                _msg.postValue("댓글이 삭제되었습니다.")
                _progressVisible.postValue(false)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다. 다시 시도해주세요!")
            }
        })
    }


    fun reportBoard(recruitmentId : Int) {
        RetrofitBuilder.apiBob.reportBoard(recruitmentId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                _msg.postValue("약속이 신고되었습니다.")
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다. 다시 시도해주세요!")
            }
        })
    }


    fun reportComment(recruitmentId : Int, commentId: Int) {
        RetrofitBuilder.apiBob.reportComment(recruitmentId, commentId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                _msg.postValue("댓글이 신고되었습니다.")
                Log.d(TAG, "response : $response")
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다. 다시 시도해주세요!")
            }
        })
    }


    fun reportReComment(recruitmentId : Int, commentId: Int, recommentId: Int) {
        RetrofitBuilder.apiBob.reportReComment(recruitmentId, commentId, recommentId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d(TAG, "reportReComment : $response")
                _msg.postValue("댓글이 신고되었습니다.")
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다. 다시 시도해주세요!")
            }
        })
    }


    fun participateBoard(recruitmentId: Int){
        _progressVisible.postValue(true)

        viewModelScope.launch {
            RetrofitBuilder.apiBob.participateBoard(recruitmentId).enqueue(object : Callback<Board> {
                override fun onResponse(call: Call<Board>, response: Response<Board>) {
                    if(response.body() != null) {
                        Log.d(TAG, "participate : $response")
                        _result.postValue(response.body())
                    }
                    _progressVisible.postValue(false)
                }

                override fun onFailure(call: Call<Board>, t: Throwable) {
                    _msg.postValue("서버에 연결이 되지 않았습니다. 다시 시도해주세요!")
                    Log.e(TAG, t.message.toString())
                }
            })
        }
    }


    fun closeBoard(recruitmentId: Int){
        _progressVisible.postValue(true)
        RetrofitBuilder.apiBob.closeBoard(recruitmentId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d(TAG, "response : $response")
                _msg.postValue("약속이 마감되었습니다")
                _progressVisible.postValue(false)
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다. 다시 시도해주세요!")
            }
        })
    }


    fun addComment(recruitmentId: Int, comment: String){
        val commentData = HashMap<String, String>()
        commentData["content"] = comment

        _progressVisible.postValue(true)
        RetrofitBuilder.apiBob.addComment(recruitmentId, commentData).enqueue(object :Callback<Comment>{
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                val code = response.code()
                if (code == 200) {
                    Log.d("addComment", "content=$comment")
                    _msg.postValue("댓글이 작성되었습니다!")
                    readBoard(recruitmentId)
                }
                _progressVisible.postValue(false)
            }

            override fun onFailure(call: Call<Comment>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다. 다시 시도해주세요!")
            }
        })
    }


    fun addReComment(recruitmentId: Int, commentId: Int, recomment: String){
        val commentData = HashMap<String, String>()
        commentData["content"] = recomment

        _progressVisible.postValue(true)
        RetrofitBuilder.apiBob.addReComment(recruitmentId,commentId, commentData).enqueue(object :Callback<Comment>{
            override fun onResponse(call: Call<Comment>, response: Response<Comment>) {
                val code = response.code()
                if (code == 200) {
                    Log.d("addReComment", "!!content=$recomment")
                    _msg.postValue("댓글이 작성되었습니다!")
                    readBoard(recruitmentId)
                }
                _progressVisible.postValue(false)
            }

            override fun onFailure(call: Call<Comment>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다. 다시 시도해주세요!")
            }
        })
    }


    fun validation(title : String, content: String, count:String, locationName: String, time: String): Boolean {
         if (title.isEmpty() || content.isEmpty()) {
             _msg.postValue("약속 제목과 내용을 입력해주세요!")
             return false
         }
         if (count == "0" || count.toString()=="") {
             _msg.postValue("약속 인원의 수를 입력해주세요!")
             return false
         }
         if (locationName.isEmpty()) {
             _msg.postValue("약속 장소를 입력해주세요!")
             return false
         }
         if (time.isEmpty()) {
             _msg.postValue("약속 시간을 입력해주세요!")
             return false
         }
         return true
    }
}