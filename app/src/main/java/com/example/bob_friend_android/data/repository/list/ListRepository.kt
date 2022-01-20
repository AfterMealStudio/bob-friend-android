package com.example.bob_friend_android.data.repository.list

import com.example.bob_friend_android.data.entity.AppointmentList
import com.example.bob_friend_android.data.entity.ErrorResponse
import com.example.bob_friend_android.data.network.NetworkResponse
import com.example.bob_friend_android.model.LocationList

interface ListRepository {
    suspend fun setAppointmentList(page: Int, type: String?, address: String?): NetworkResponse<AppointmentList, ErrorResponse>
    suspend fun searchAppointmentList(page: Int, category: String, keyword: String, start: String?, end: String?): NetworkResponse<AppointmentList, ErrorResponse>
    suspend fun setAppointmentLocationList(zoom: Int, longitude: Double, latitude:Double) : NetworkResponse<LocationList, ErrorResponse>
}