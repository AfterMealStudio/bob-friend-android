package com.example.bob_friend_android.data.repository.appointment

import com.example.bob_friend_android.data.entity.Board
import com.example.bob_friend_android.data.entity.Comment
import com.example.bob_friend_android.data.entity.ErrorResponse
import com.example.bob_friend_android.data.network.NetworkResponse
import com.example.bob_friend_android.data.network.api.ApiService
import okhttp3.ResponseBody
import javax.inject.Inject

class AppointmentRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AppointmentRepository {
    override suspend fun createAppointment(board: Board): NetworkResponse<Board, ErrorResponse> {
        return apiService.createAppointmentResponse(board)
    }

    override suspend fun deleteAppointment(appointmentId: Int): NetworkResponse<ResponseBody, ErrorResponse> {
        return apiService.deleteAppointmentResponse(appointmentId)
    }

    override suspend fun reportAppointment(appointmentId: Int): NetworkResponse<ResponseBody, ErrorResponse> {
        return apiService.reportAppointmentResponse(appointmentId)
    }

    override suspend fun joinAppointment(appointmentId: Int): NetworkResponse<Board, ErrorResponse> {
        return apiService.joinAppointmentResponse(appointmentId)
    }

    override suspend fun closeAppointment(appointmentId: Int): NetworkResponse<ResponseBody, ErrorResponse> {
        return apiService.closeAppointmentResponse(appointmentId)
    }

    override suspend fun setAppointment(appointmentId: Int): NetworkResponse<Board, ErrorResponse> {
        return apiService.getAppointmentResponse(appointmentId)
    }

    override suspend fun createComment(
        appointmentId: Int,
        comment: HashMap<String, String>,
        commentId: Int?
    ): NetworkResponse<Comment, ErrorResponse> {
        return if (commentId == null) {
            apiService.createCommentResponse(appointmentId, comment)
        } else {
            apiService.createReCommentResponse(appointmentId, commentId, comment)
        }
    }

    override suspend fun deleteComment(
        appointmentId: Int,
        commentId: Int,
        reCommentId: Int?
    ): NetworkResponse<ResponseBody, ErrorResponse> {
        return if (reCommentId == null) {
            apiService.deleteCommentResponse(appointmentId, commentId)
        } else {
            apiService.deleteReCommentResponse(appointmentId, commentId, reCommentId)
        }
    }

    override suspend fun reportComment(
        appointmentId: Int,
        commentId: Int,
        reCommentId: Int?
    ): NetworkResponse<ResponseBody, ErrorResponse> {
        return if (reCommentId == null) {
            apiService.reportCommentResponse(appointmentId, commentId)
        } else {
            apiService.reportReCommentResponse(appointmentId, commentId, reCommentId)
        }
    }
}