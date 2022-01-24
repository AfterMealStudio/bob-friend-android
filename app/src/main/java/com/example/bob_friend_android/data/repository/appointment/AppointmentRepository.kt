package com.example.bob_friend_android.data.repository.appointment

import com.example.bob_friend_android.data.entity.Board
import com.example.bob_friend_android.data.entity.Comment
import com.example.bob_friend_android.data.entity.ErrorResponse
import com.example.bob_friend_android.data.network.NetworkResponse

interface AppointmentRepository {
    suspend fun createAppointment(board: Board) : NetworkResponse<Board, ErrorResponse>
    suspend fun setAppointment(appointmentId: Int) : NetworkResponse<Board, ErrorResponse>
    suspend fun deleteAppointment(appointmentId: Int) : NetworkResponse<Void, ErrorResponse>
    suspend fun reportAppointment(appointmentId: Int) : NetworkResponse<Void, ErrorResponse>
    suspend fun joinAppointment(appointmentId: Int) : NetworkResponse<Board, ErrorResponse>
    suspend fun closeAppointment(appointmentId: Int) : NetworkResponse<Void, ErrorResponse>

    suspend fun createComment(appointmentId: Int, comment: HashMap<String, String>, commentId: Int?) : NetworkResponse<Comment, ErrorResponse>
    suspend fun deleteComment(appointmentId: Int, commentId : Int, reCommentId: Int?) : NetworkResponse<Void, ErrorResponse>
    suspend fun reportComment(appointmentId: Int, commentId: Int, reCommentId: Int?) : NetworkResponse<Void, ErrorResponse>
}