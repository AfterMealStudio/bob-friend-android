package com.example.bob_friend_android.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.bob_friend_android.ui.view.base.BaseViewModel
import com.example.bob_friend_android.data.entity.Board
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
    val result : LiveData<Board>
        get() = _result

    private val _msg = MutableLiveData<String>()
    val errorMsg : LiveData<String>
        get() = _msg


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
            when (response) {
                is NetworkResponse.Success -> {
                    _msg.postValue("약속이 작성되었습니다!")
                }
                is NetworkResponse.ApiError -> {
                    _msg.postValue("Api 오류 : 약속생성을 실패했습니다.")
                }
                is NetworkResponse.NetworkError -> {
                    _msg.postValue("서버 오류 : 약속생성을 실패했습니다.")
                }
                is NetworkResponse.UnknownError -> {
                    _msg.postValue("알 수 없는 오류 : 약속생성을 실패했습니다.")
                }
            }
            hideProgress()
        }
    }


    fun setAppointment(appointmentId: Int){
        showProgress()
        viewModelScope.launch {
            val response = repository.setAppointment(appointmentId)
            when (response) {
                is NetworkResponse.Success -> {
                    _result.postValue(response.body)
                }
                is NetworkResponse.ApiError -> {
                    _msg.postValue("Api 오류 : 약속조회를 실패했습니다.")
                }
                is NetworkResponse.NetworkError -> {
                    _msg.postValue("서버 오류 : 약속조회를 실패했습니다.")
                }
                is NetworkResponse.UnknownError -> {
                    _msg.postValue("알 수 없는 오류 : 약속조회를 실패했습니다.")
                }
            }
            hideProgress()
        }
    }


    fun deleteAppointment(appointmentId : Int) {
        showProgress()
        viewModelScope.launch {
            val response = repository.deleteAppointment(appointmentId)
            when (response) {
                is NetworkResponse.Success -> {
                    _msg.postValue("약속이 삭제되었습니다.")
                }
                is NetworkResponse.ApiError -> {
                    _msg.postValue("Api 오류 : 약속삭제를 실패했습니다.")
                }
                is NetworkResponse.NetworkError -> {
                    _msg.postValue("서버 오류 : 약속삭제를 실패했습니다.")
                }
                is NetworkResponse.UnknownError -> {
                    _msg.postValue("알 수 없는 오류 : 약속삭제를 실패했습니다.")
                }
            }
            hideProgress()
        }
    }


    fun reportAppointment(appointmentId : Int) {
        showProgress()
        viewModelScope.launch {
            val response = repository.reportAppointment(appointmentId)
            when (response) {
                is NetworkResponse.Success -> {
                    _msg.postValue("약속이 신고되었습니다.")
                }
                is NetworkResponse.ApiError -> {
                    _msg.postValue("Api 오류 : 약속신고를 실패했습니다.")
                }
                is NetworkResponse.NetworkError -> {
                    _msg.postValue("서버 오류 : 약속신고를 실패했습니다.")
                }
                is NetworkResponse.UnknownError -> {
                    _msg.postValue("알 수 없는 오류 : 약속신고를 실패했습니다.")
                }
            }
            hideProgress()
        }
    }


    fun joinAppointment(appointmentId: Int){
        showProgress()
        viewModelScope.launch {
            val response = repository.joinAppointment(appointmentId)
            when (response) {
                is NetworkResponse.Success -> {
                    _result.postValue(response.body)
                }
                is NetworkResponse.ApiError -> {
                    _msg.postValue("Api 오류 : 약속참가에 실패했습니다.")
                }
                is NetworkResponse.NetworkError -> {
                    _msg.postValue("서버 오류 : 약속참가에 실패했습니다.")
                }
                is NetworkResponse.UnknownError -> {
                    _msg.postValue("알 수 없는 오류 : 참가할 수 없는 약속입니다.")
                }
            }
            hideProgress()
        }
    }


    fun closeAppointment(appointmentId: Int){
        showProgress()
        viewModelScope.launch {
            val response = repository.closeAppointment(appointmentId)
            when (response) {
                is NetworkResponse.Success -> {
                    _msg.postValue("약속이 마감되었습니다.")
                }
                is NetworkResponse.ApiError -> {
                    _msg.postValue("Api 오류 : 약속마감을 실패했습니다.")
                }
                is NetworkResponse.NetworkError -> {
                    _msg.postValue("서버 오류 : 약속마감을 실패했습니다.")
                }
                is NetworkResponse.UnknownError -> {
                    _msg.postValue("알 수 없는 오류 : 약속마감을 실패했습니다.")
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
            when (response) {
                is NetworkResponse.Success -> {
                    _msg.postValue("댓글이 작성되었습니다.")
                }
                is NetworkResponse.ApiError -> {
                    _msg.postValue("Api 오류 : 댓글작성을 실패했습니다.")
                }
                is NetworkResponse.NetworkError -> {
                    _msg.postValue("서버 오류 : 댓글작성을 실패했습니다.")
                }
                is NetworkResponse.UnknownError -> {
                    _msg.postValue("알 수 없는 오류 : 댓글작성을 실패했습니다.")
                }
            }
            hideProgress()
        }
    }


    fun deleteComment(appointmentId: Int, commentId : Int, reCommentId: Int? = null) {
        showProgress()
        viewModelScope.launch {
            val response = repository.deleteComment(appointmentId, commentId, reCommentId)
            when (response) {
                is NetworkResponse.Success -> {
                    _msg.postValue("댓글이 삭제되었습니다.")
                }
                is NetworkResponse.ApiError -> {
                    _msg.postValue("Api 오류 : 댓글삭제를 실패했습니다.")
                }
                is NetworkResponse.NetworkError -> {
                    _msg.postValue("서버 오류 : 댓글삭제를 실패했습니다.")
                }
                is NetworkResponse.UnknownError -> {
                    _msg.postValue("알 수 없는 오류 : 댓글삭제를 실패했습니다.")
                }
            }
            hideProgress()
        }
    }


    fun reportComment(appointmentId : Int, commentId: Int, reCommentId: Int? = null) {
        showProgress()
        viewModelScope.launch {
            val response = repository.reportComment(appointmentId, commentId, reCommentId)
            when (response) {
                is NetworkResponse.Success -> {
                    _msg.postValue("댓글이 신고되었습니다.")
                }
                is NetworkResponse.ApiError -> {
                    _msg.postValue("Api 오류 : 댓글신고를 실패했습니다.")
                }
                is NetworkResponse.NetworkError -> {
                    _msg.postValue("서버 오류 : 댓글신고를 실패했습니다.")
                }
                is NetworkResponse.UnknownError -> {
                    _msg.postValue("알 수 없는 오류 : 댓글신고를 실패했습니다.")
                }
            }
            hideProgress()
        }
    }
}