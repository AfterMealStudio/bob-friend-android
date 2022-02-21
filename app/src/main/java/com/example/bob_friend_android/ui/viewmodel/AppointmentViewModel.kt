package com.example.bob_friend_android.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bob_friend_android.ui.view.base.BaseViewModel
import com.example.bob_friend_android.data.entity.Board
import com.example.bob_friend_android.data.entity.Event
import com.example.bob_friend_android.data.network.NetworkResponse
import com.example.bob_friend_android.data.repository.appointment.AppointmentRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppointmentViewModel @Inject constructor(
    private val repository: AppointmentRepository
): BaseViewModel() {

    private val _result = MutableLiveData<Board>()
    val result : LiveData<Board> = _result

    private val _msg = MutableLiveData<Event<String>>()
    val errorMsg : LiveData<Event<String>> = _msg


    private fun postValueEvent(value : Int, type: String) {
        val msgArrayList = arrayOf("Api 오류 : $type 실패했습니다.",
            "서버 오류 : $type 실패했습니다.",
            "알 수 없는 오류 : $type 실패했습니다.",
            "${type}이 작성되었습니다.",
            "${type}이 삭제되었습니다.",
            "${type}이 신고되었습니다.",
            "${type}이 마감되었습니다."
        )

        when(value) {
            0 -> _msg.postValue(Event(msgArrayList[0]))
            1 -> _msg.postValue(Event(msgArrayList[1]))
            2 -> _msg.postValue(Event(msgArrayList[2]))
            3 -> _msg.postValue(Event(msgArrayList[3]))
            4 -> _msg.postValue(Event(msgArrayList[4]))
            5 -> _msg.postValue(Event(msgArrayList[5]))
            6 -> _msg.postValue(Event(msgArrayList[6]))
        }
    }


    fun createAppointment(title : String, content: String, count:String, address: String, locationName: String, x: Double?, y: Double?, time: String,
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

        showProgress()
        viewModelScope.launch {
            val response = repository.createAppointment(board)
            val type = "약속생성에"
            when (response) {
                is NetworkResponse.Success -> {
                    postValueEvent(3, "약속")
                }
                is NetworkResponse.ApiError -> {
                    postValueEvent(0, type)
                }
                is NetworkResponse.NetworkError -> {
                    postValueEvent(1, type)
                }
                is NetworkResponse.UnknownError -> {
                    postValueEvent(2, type)
                }
            }
            hideProgress()
        }
    }


    fun setAppointment(appointmentId: Int){
        showProgress()
        viewModelScope.launch {
            val response = repository.setAppointment(appointmentId)
            val type = "약속조회에"
            when (response) {
                is NetworkResponse.Success -> {
                    _result.postValue(response.body)
                }
                is NetworkResponse.ApiError -> {
                    postValueEvent(0, type)
                }
                is NetworkResponse.NetworkError -> {
                    postValueEvent(1, type)
                }
                is NetworkResponse.UnknownError -> {
                    postValueEvent(2, type)
                }
            }
            hideProgress()
        }
    }


    fun deleteAppointment(appointmentId : Int) {
        showProgress()
        viewModelScope.launch {
            val response = repository.deleteAppointment(appointmentId)
            val type = "약속삭제에"
            when (response) {
                is NetworkResponse.Success -> {
                    postValueEvent(4, "약속")
                }
                is NetworkResponse.ApiError -> {
                    postValueEvent(0, type)
                }
                is NetworkResponse.NetworkError -> {
                    postValueEvent(1, type)
                }
                is NetworkResponse.UnknownError -> {
                    postValueEvent(2, type)
                }
            }
            hideProgress()
        }
    }


    fun reportAppointment(appointmentId : Int) {
        showProgress()
        viewModelScope.launch {
            val response = repository.reportAppointment(appointmentId)
            val type = "약속신고에"
            when (response) {
                is NetworkResponse.Success -> {
                    postValueEvent(5, "약속")
                }
                is NetworkResponse.ApiError -> {
                    postValueEvent(0, type)
                }
                is NetworkResponse.NetworkError -> {
                    postValueEvent(1, type)
                }
                is NetworkResponse.UnknownError -> {
                    postValueEvent(2, type)
                }
            }
            hideProgress()
        }
    }


    fun joinAppointment(appointmentId: Int){
        showProgress()
        viewModelScope.launch {
            val response = repository.joinAppointment(appointmentId)
            val type = "참가 및 취소에"
            when (response) {
                is NetworkResponse.Success -> {
                    _result.postValue(response.body)
                }
                is NetworkResponse.ApiError -> {
                    postValueEvent(0, type)
                }
                is NetworkResponse.NetworkError -> {
                    postValueEvent(1, type)
                }
                is NetworkResponse.UnknownError -> {
                    postValueEvent(2, type)
                }
            }
            hideProgress()
        }
    }


    fun closeAppointment(appointmentId: Int){
        showProgress()
        viewModelScope.launch {
            val response = repository.closeAppointment(appointmentId)
            val type = "약속마감에"
            when (response) {
                is NetworkResponse.Success -> {
                    postValueEvent(6, "약속")
                }
                is NetworkResponse.ApiError -> {
                    postValueEvent(0, type)
                }
                is NetworkResponse.NetworkError -> {
                    postValueEvent(1, type)
                }
                is NetworkResponse.UnknownError -> {
                    postValueEvent(2, type)
                }
            }
            hideProgress()
        }
    }


    fun createComment(appointmentId: Int, comment: String, commentId: Int? = null){
        val commentData = HashMap<String, String>()
        commentData["content"] = comment

        showProgress()
        viewModelScope.launch {
            val response = repository.createComment(appointmentId, commentData, commentId)
            val type = "댓글작성에"
            when (response) {
                is NetworkResponse.Success -> {
                    postValueEvent(3, "댓글")
                }
                is NetworkResponse.ApiError -> {
                    postValueEvent(0, type)
                }
                is NetworkResponse.NetworkError -> {
                    postValueEvent(1, type)
                }
                is NetworkResponse.UnknownError -> {
                    postValueEvent(2, type)
                }
            }
            hideProgress()
        }
    }


    fun deleteComment(appointmentId: Int, commentId : Int, reCommentId: Int? = null) {
        showProgress()
        viewModelScope.launch {
            val response = repository.deleteComment(appointmentId, commentId, reCommentId)
            val type = "댓글삭제에"
            when (response) {
                is NetworkResponse.Success -> {
                    postValueEvent(4, "댓글")
                }
                is NetworkResponse.ApiError -> {
                    postValueEvent(0, type)
                }
                is NetworkResponse.NetworkError -> {
                    postValueEvent(1, type)
                }
                is NetworkResponse.UnknownError -> {
                    postValueEvent(2, type)
                }
            }
            hideProgress()
        }
    }


    fun reportComment(appointmentId : Int, commentId: Int, reCommentId: Int? = null) {
        showProgress()
        viewModelScope.launch {
            val response = repository.reportComment(appointmentId, commentId, reCommentId)
            val type = "댓글신고에"
            when (response) {
                is NetworkResponse.Success -> {
                    postValueEvent(5, "댓글")
                }
                is NetworkResponse.ApiError -> {
                    postValueEvent(0, type)
                }
                is NetworkResponse.NetworkError -> {
                    postValueEvent(1, type)
                }
                is NetworkResponse.UnknownError -> {
                    postValueEvent(2, type)
                }
            }
            hideProgress()
        }
    }
}