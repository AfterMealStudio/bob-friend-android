package com.example.bob_friend_android

import android.content.Context
import android.util.Log
import android.view.View
import android.view.ViewGroup
import net.daum.mf.map.api.MapView

class KakaoMap private constructor() {
    companion object {
        private var instance: KakaoMap? = null

        private lateinit var mapView:MapView
        private lateinit var context: Context

        fun getInstance(_context: Context): KakaoMap {
            return instance ?: synchronized(this) {
                instance ?: KakaoMap().also {
                    context = _context
                    instance = it
                }
            }
        }
    }

    fun setMapView() {
        mapView = MapView(context)
    }
}
