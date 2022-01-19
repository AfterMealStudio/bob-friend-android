package com.example.bob_friend_android.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bob_friend_android.ui.view.base.BaseViewModel
import com.example.bob_friend_android.data.entity.Board
import com.example.bob_friend_android.data.entity.Comment
import com.example.bob_friend_android.di.ApiModule
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BoardViewModel: BaseViewModel() {

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
        ApiModule.apiBob.addRecruitments(board).enqueue(object : Callback<Board> {
            override fun onResponse(call: Call<Board>, response: Response<Board>) {
                val code = response.code()
                Log.d(TAG, "createBoard: title=$title, content=$content, responseCode: ${response.code()}, error : ${response.errorBody().toString()}")
                when (code) {
                    200 -> {
                        _msg.postValue("약속이 작성되었습니다!")
                    }
                    403 -> {
                        _msg.postValue("토큰이 만료되었습니다.")
                    }
                    else -> {
                        _msg.postValue("다시 시도해주세요. ${response.errorBody()!!.string()}")
                    }
                }
                _progressVisible.postValue(false)
            }

            override fun onFailure(call: Call<Board>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다. 다시 시도해주세요!")
                Log.e("AddViewModel!!!", t.message.toString())
            }
        })
    }


    fun readBoard(recruitmentId: Int){
        _progressVisible.postValue(true)
        viewModelScope.launch {
            ApiModule.apiBob.getRecruitment(recruitmentId).enqueue(object : Callback<Board> {
                override fun onResponse(call: Call<Board>, response: Response<Board>) {
                    Log.d(TAG, "readBoard : ${response.code()}")
                    when (response.code()) {
                        200 -> _result.postValue(response.body())
                        403 -> _msg.postValue("접근할 수 없는 약속")
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
        ApiModule.apiBob.deleteRecruitment(recruitmentId).enqueue(object : Callback<Void> {
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
        ApiModule.apiBob.deleteComment(recruitmentId,commentId).enqueue(object : Callback<Void> {
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
        ApiModule.apiBob.deleteReComment(recruitmentId,commentId,recommentId).enqueue(object : Callback<Void> {
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
        ApiModule.apiBob.reportBoard(recruitmentId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                _msg.postValue("약속이 신고되었습니다.")
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                _msg.postValue("서버에 연결이 되지 않았습니다. 다시 시도해주세요!")
            }
        })
    }


    fun reportComment(recruitmentId : Int, commentId: Int) {
        ApiModule.apiBob.reportComment(recruitmentId, commentId).enqueue(object : Callback<Void> {
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
        ApiModule.apiBob.reportReComment(recruitmentId, commentId, recommentId).enqueue(object : Callback<Void> {
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
            ApiModule.apiBob.participateBoard(recruitmentId).enqueue(object : Callback<Board> {
                override fun onResponse(call: Call<Board>, response: Response<Board>) {
                    when (response.code()) {
                        200 -> {
                            _result.postValue(response.body())
                            _msg.postValue("약속 참가 기능")
                        }
                        403 -> _msg.postValue("참가할 수 없는 약속")
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
        ApiModule.apiBob.closeBoard(recruitmentId).enqueue(object : Callback<Void> {
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
        ApiModule.apiBob.addComment(recruitmentId, commentData).enqueue(object :Callback<Comment>{
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
        ApiModule.apiBob.addReComment(recruitmentId,commentId, commentData).enqueue(object :Callback<Comment>{
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
}